package com.drivenote.app.data.mapper

import com.drivenote.app.data.remote.dto.NoteDto
import com.drivenote.app.domain.model.Note

fun Note.toDto(): NoteDto {
    return NoteDto(
        localId = id,
        text = text,
        category = category.label,
        tags = tags,
        createdAt = createdAt,
        audioPath = audioPath
    )
}
