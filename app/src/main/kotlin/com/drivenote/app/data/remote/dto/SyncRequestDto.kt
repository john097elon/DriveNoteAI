package com.drivenote.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncRequestDto(
    @SerialName("device_id") val deviceId: String,
    val notes: List<NoteDto>
)
