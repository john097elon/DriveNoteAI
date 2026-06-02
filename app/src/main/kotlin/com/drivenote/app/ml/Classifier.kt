package com.drivenote.app.ml

interface Classifier {
    suspend fun classify(text: String): ClassificationResult
    fun isAvailable(): Boolean
}
