package com.drivenote.app.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SttManager @Inject constructor() {
    suspend fun transcribe(context: Context): Result<String> = suspendCancellableCoroutine { continuation ->
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            continuation.resume(Result.failure(IllegalStateException("음성 인식을 사용할 수 없습니다.")))
            return@suspendCancellableCoroutine
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                if (continuation.isActive) {
                    continuation.resume(
                        if (text.isBlank()) {
                            Result.failure(IllegalStateException("음성 인식 결과가 비어 있습니다."))
                        } else {
                            Result.success(text)
                        }
                    )
                }
                recognizer.destroy()
            }

            override fun onError(error: Int) {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(IllegalStateException("STT 오류 코드: $error")))
                }
                recognizer.destroy()
            }

            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })

        recognizer.startListening(intent)
        continuation.invokeOnCancellation {
            recognizer.cancel()
            recognizer.destroy()
        }
    }
}
