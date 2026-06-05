package com.drivenote.app.domain.usecase

import android.content.Context
import android.os.SystemClock
import com.drivenote.app.audio.RealtimeSttManager
import com.drivenote.app.audio.SentenceBuffer
import com.drivenote.app.audio.TtsController
import com.drivenote.app.data.remote.HermesStreamClient
import com.drivenote.app.data.remote.dto.ChatCompletionMessageDto
import com.drivenote.app.domain.model.ConversationTurn
import com.drivenote.app.domain.model.VoiceConversationPhase
import com.drivenote.app.domain.model.VoiceConversationState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Singleton
class VoiceConversationOrchestrator @Inject constructor(
    private val sttManager: RealtimeSttManager,
    private val ttsController: TtsController,
    private val hermesStreamClient: HermesStreamClient
) {
    private val _state = MutableStateFlow(VoiceConversationState())
    val state: StateFlow<VoiceConversationState> = _state.asStateFlow()

    private val history = mutableListOf<ConversationTurn>()

    private var isStarted = false
    private var appContext: Context? = null
    private var scope: CoroutineScope? = null
    private var sttEventsJob: Job? = null
    private var streamJob: Job? = null
    private var restartJob: Job? = null
    private var lastUtteranceId: String? = null
    private var ignoreBargeInUntilMs: Long = 0L

    fun start(context: Context, serviceScope: CoroutineScope) {
        if (isStarted) return

        isStarted = true
        appContext = context.applicationContext
        scope = serviceScope
        history.clear()
        lastUtteranceId = null
        ignoreBargeInUntilMs = 0L

        ttsController.initialize()
        _state.value = VoiceConversationState(
            phase = VoiceConversationPhase.LISTENING,
            isActive = true
        )

        sttEventsJob = serviceScope.launch {
            sttManager.events.collectLatest { event ->
                handleSpeechEvent(event)
            }
        }

        startListeningNow()
    }

    fun stop() {
        if (!isStarted) return

        restartJob?.cancel()
        streamJob?.cancel()
        sttEventsJob?.cancel()
        sttManager.stopListening()
        ttsController.shutdown()

        history.clear()
        lastUtteranceId = null
        ignoreBargeInUntilMs = 0L
        isStarted = false
        appContext = null
        scope = null

        _state.value = VoiceConversationState()
    }

    private suspend fun handleSpeechEvent(event: RealtimeSttManager.SpeechEvent) {
        if (!isStarted) return

        when (event) {
            is RealtimeSttManager.SpeechEvent.Partial -> {
                if (_state.value.phase == VoiceConversationPhase.LISTENING) {
                    _state.update { current ->
                        current.copy(
                            phase = VoiceConversationPhase.LISTENING,
                            isActive = true,
                            partialUserText = event.text,
                            errorMessage = null
                        )
                    }
                }
            }

            is RealtimeSttManager.SpeechEvent.Final -> {
                if (_state.value.phase == VoiceConversationPhase.LISTENING) {
                    beginAssistantStreaming(event.text)
                }
            }

            is RealtimeSttManager.SpeechEvent.SpeechStarted -> {
                val phase = _state.value.phase
                if (phase == VoiceConversationPhase.PROCESSING || phase == VoiceConversationPhase.SPEAKING) {
                    val now = SystemClock.elapsedRealtime()
                    if (now >= ignoreBargeInUntilMs) {
                        handleBargeIn()
                    }
                }
            }

            is RealtimeSttManager.SpeechEvent.Error -> {
                handleSttError(event.code)
            }
        }
    }

    private fun beginAssistantStreaming(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || !isStarted) return

        sttManager.stopListening()
        restartJob?.cancel()
        streamJob?.cancel()

        history.add(ConversationTurn(role = "user", content = trimmed))
        _state.update { current ->
            current.copy(
                phase = VoiceConversationPhase.PROCESSING,
                isActive = true,
                partialUserText = "",
                finalUserText = trimmed,
                errorMessage = null
            )
        }

        startBargeInMonitoring(delayMs = BARGE_IN_LISTEN_DELAY_MS)

        val currentScope = scope ?: return
        streamJob = currentScope.launch {
            val sentenceBuffer = SentenceBuffer()
            val assistantBuilder = StringBuilder()

            try {
                val messages = history.map {
                    ChatCompletionMessageDto(role = it.role, content = it.content)
                }

                hermesStreamClient.streamChat(messages).collectLatest { token ->
                    assistantBuilder.append(token)
                    _state.update { current ->
                        current.copy(
                            phase = VoiceConversationPhase.SPEAKING,
                            isActive = true,
                            assistantText = assistantBuilder.toString(),
                            errorMessage = null
                        )
                    }

                    sentenceBuffer.append(token)?.let { sentence ->
                        val utteranceId = ttsController.speak(sentence, flushQueue = false)
                        if (utteranceId != null) {
                            lastUtteranceId = utteranceId
                            ignoreBargeInUntilMs = SystemClock.elapsedRealtime() + BARGE_IN_GUARD_MS
                        }
                    }
                }

                sentenceBuffer.flush()?.let { remaining ->
                    val utteranceId = ttsController.speak(remaining, flushQueue = false)
                    if (utteranceId != null) {
                        lastUtteranceId = utteranceId
                        ignoreBargeInUntilMs = SystemClock.elapsedRealtime() + BARGE_IN_GUARD_MS
                    }
                }

                if (assistantBuilder.isNotBlank()) {
                    history.add(ConversationTurn(role = "assistant", content = assistantBuilder.toString()))
                }

                val waitUtteranceId = lastUtteranceId
                if (!waitUtteranceId.isNullOrBlank()) {
                    withTimeoutOrNull(TTS_WAIT_TIMEOUT_MS) {
                        ttsController.utteranceCompleted.first { it == waitUtteranceId }
                    }
                }

                if (isStarted && _state.value.phase != VoiceConversationPhase.LISTENING) {
                    sttManager.stopListening()
                    delay(LISTEN_RESTART_DELAY_MS)
                    startListeningNow()
                }
            } catch (_: CancellationException) {
                // Barge-in or stop() will cancel this job.
            } catch (t: Throwable) {
                _state.update { current ->
                    current.copy(
                        phase = VoiceConversationPhase.ERROR,
                        isActive = true,
                        errorMessage = t.message ?: "Hermes 응답 처리 중 오류가 발생했습니다."
                    )
                }
                delay(ERROR_RECOVERY_DELAY_MS)
                if (isStarted) {
                    startListeningNow()
                }
            } finally {
                sentenceBuffer.clear()
            }
        }
    }

    private fun handleBargeIn() {
        if (!isStarted) return

        streamJob?.cancel()
        ttsController.stop()
        lastUtteranceId = null
        ignoreBargeInUntilMs = SystemClock.elapsedRealtime() + BARGE_IN_GUARD_MS

        _state.update { current ->
            current.copy(
                phase = VoiceConversationPhase.LISTENING,
                isActive = true,
                partialUserText = "",
                finalUserText = "",
                errorMessage = null
            )
        }
    }

    private fun handleSttError(code: Int) {
        if (!isStarted) return

        when (_state.value.phase) {
            VoiceConversationPhase.LISTENING -> {
                scheduleListeningRestart()
            }

            VoiceConversationPhase.PROCESSING,
            VoiceConversationPhase.SPEAKING -> {
                startBargeInMonitoring(delayMs = BARGE_IN_LISTEN_DELAY_MS)
            }

            VoiceConversationPhase.ERROR,
            VoiceConversationPhase.IDLE -> Unit
        }
    }

    private fun scheduleListeningRestart() {
        restartJob?.cancel()
        val currentScope = scope ?: return
        restartJob = currentScope.launch {
            delay(LISTEN_RESTART_DELAY_MS)
            if (isStarted && _state.value.phase == VoiceConversationPhase.LISTENING) {
                startListeningNow()
            }
        }
    }

    private fun startBargeInMonitoring(delayMs: Long) {
        restartJob?.cancel()
        val currentScope = scope ?: return
        restartJob = currentScope.launch {
            delay(delayMs)
            val phase = _state.value.phase
            if (isStarted && (phase == VoiceConversationPhase.PROCESSING || phase == VoiceConversationPhase.SPEAKING)) {
                appContext?.let { sttManager.startListening(it) }
            }
        }
    }

    private fun startListeningNow() {
        val context = appContext ?: return
        _state.update { current ->
            current.copy(
                phase = VoiceConversationPhase.LISTENING,
                isActive = true,
                partialUserText = "",
                finalUserText = "",
                errorMessage = null
            )
        }
        sttManager.startListening(context)
    }

    private companion object {
        const val BARGE_IN_GUARD_MS = 600L
        const val BARGE_IN_LISTEN_DELAY_MS = 200L
        const val LISTEN_RESTART_DELAY_MS = 350L
        const val ERROR_RECOVERY_DELAY_MS = 350L
        const val TTS_WAIT_TIMEOUT_MS = 15_000L
    }
}
