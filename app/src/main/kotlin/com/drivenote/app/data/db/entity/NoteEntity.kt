package com.drivenote.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val text: String,
    val category: String = "미분류",
    val tags: List<String> = emptyList(),
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    val source: String = "voice",
    @ColumnInfo(name = "audio_path") val audioPath: String? = null,
    @ColumnInfo(name = "is_classified") val isClassified: Boolean = false,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "hermes_id") val hermesId: String? = null
)
