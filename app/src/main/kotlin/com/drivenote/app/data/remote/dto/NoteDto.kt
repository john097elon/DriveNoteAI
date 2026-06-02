package com.drivenote.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    @SerialName("local_id") val localId: String,
    val text: String,
    val category: String,
    val tags: List<String>,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("audio_path") val audioPath: String? = null
)
