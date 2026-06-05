package com.drivenote.app.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class RealtimeSttManager @Inject constructor() {

    sealed class SpeechEvent {
        data class Partial(val text: String) : SpeechEvent()
        data class Final(val text: String) : SpeechEvent()
        object SpeechStarted : SpeechEvent()
        data class Error(val code: Int) : SpeechEvent()
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var recognizer: SpeechRecognizer? = null

    private val _events = MutableSharedFlow<SpeechEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<SpeechEvent> = _events.asSharedFlow()

    fun startListening(context: Context) {
        val appContext = context.applicationContext
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
                _events.tryEmit(SpeechEvent.Error(-1))
                return@post
            }

            recognizer?.cancel()
            recognizer?.destroy()

            recognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
                setRecognitionListener(createListener())
                startListening(createIntent())
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            recognizer?.stopListening()
            recognizer?.cancel()
            recognizer?.destroy()
            recognizer = null
        }
    }

    private fun createIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 700L)
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit

            override fun onBeginningOfSpeech() {
                _events.tryEmit(SpeechEvent.SpeechStarted)
            }

            override fun onRmsChanged(rmsdB: Float) = Unit

            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() = Unit

            override fun onError(error: Int) {
                _events.tryEmit(SpeechEvent.Error(error))
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()
                    .orEmpty()
                if (text.isNotBlank()) {
                    _events.tryEmit(SpeechEvent.Final(text))
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()
                    .orEmpty()
                if (text.isNotBlank()) {
                    _events.tryEmit(SpeechEvent.Partial(text))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
    }
}
