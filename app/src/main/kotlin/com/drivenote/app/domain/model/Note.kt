package com.drivenote.app.domain.model

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val category: NoteCategory = NoteCategory.UNCLASSIFIED,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val source: NoteSource = NoteSource.VOICE,
    val audioPath: String? = null,
    val isClassified: Boolean = false,
    val isSynced: Boolean = false,
    val hermesId: String? = null
)
