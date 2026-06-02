package com.drivenote.app.ml

import com.drivenote.app.domain.model.NoteCategory

data class ClassificationResult(
    val category: NoteCategory,
    val tags: List<String>,
    val confidence: Float
)
