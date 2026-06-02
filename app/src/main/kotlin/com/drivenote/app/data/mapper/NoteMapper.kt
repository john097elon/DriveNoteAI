package com.drivenote.app.data.mapper

import com.drivenote.app.data.db.entity.NoteEntity
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.model.NoteSource

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        text = text,
        category = NoteCategory.fromLabel(category),
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        source = NoteSource.valueOf(source.uppercase()),
        audioPath = audioPath,
        isClassified = isClassified,
        isSynced = isSynced,
        hermesId = hermesId
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        text = text,
        category = category.label,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        source = source.name.lowercase(),
        audioPath = audioPath,
        isClassified = isClassified,
        isSynced = isSynced,
        hermesId = hermesId
    )
}
