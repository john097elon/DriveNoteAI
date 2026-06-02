package com.drivenote.app.ml

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QwenClassifier @Inject constructor(
    private val fallbackKeywordClassifier: FallbackKeywordClassifier
) : Classifier {

    override suspend fun classify(text: String): ClassificationResult {
        // MVP placeholder for future TFLite/LiteRT Qwen2.5-0.5B integration.
        return fallbackKeywordClassifier.classify(text)
    }

    override fun isAvailable(): Boolean = false
}
