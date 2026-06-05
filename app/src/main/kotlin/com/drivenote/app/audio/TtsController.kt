package com.drivenote.app.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class TtsController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val utteranceIdCounter = AtomicLong(0)
    private val pendingUtterances = AtomicInteger(0)

    private var tts: TextToSpeech? = null

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _utteranceCompleted = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val utteranceCompleted: SharedFlow<String> = _utteranceCompleted.asSharedFlow()

    fun initialize() {
        mainHandler.post {
            if (tts != null) return@post

            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.KOREAN
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            markUtteranceComplete(utteranceId)
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            markUtteranceComplete(utteranceId)
                        }
                    })
                    _isReady.value = true
                }
            }
        }
    }

    fun speak(text: String, flushQueue: Boolean = false): String? {
        if (text.isBlank()) return null
        if (!_isReady.value) return null

        val utteranceId = "voice_utt_${utteranceIdCounter.incrementAndGet()}"
        mainHandler.post {
            val queueMode = if (flushQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            val result = tts?.speak(text, queueMode, null, utteranceId)
            if (result == TextToSpeech.SUCCESS) {
                pendingUtterances.incrementAndGet()
                _isSpeaking.value = true
            }
        }
        return utteranceId
    }

    fun stop() {
        mainHandler.post {
            tts?.stop()
            pendingUtterances.set(0)
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        mainHandler.post {
            tts?.stop()
            tts?.shutdown()
            tts = null
            pendingUtterances.set(0)
            _isReady.value = false
            _isSpeaking.value = false
        }
    }

    private fun markUtteranceComplete(utteranceId: String?) {
        val updated = decrementPendingUtterances()
        if (updated == 0) {
            _isSpeaking.value = false
        }
        if (!utteranceId.isNullOrBlank()) {
            _utteranceCompleted.tryEmit(utteranceId)
        }
    }

    private fun decrementPendingUtterances(): Int {
        while (true) {
            val current = pendingUtterances.get()
            if (current <= 0) return 0
            val next = current - 1
            if (pendingUtterances.compareAndSet(current, next)) {
                return next
            }
        }
    }
}
