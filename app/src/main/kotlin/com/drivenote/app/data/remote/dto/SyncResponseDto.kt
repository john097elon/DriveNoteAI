package com.drivenote.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncResponseDto(
    val success: Boolean,
    @SerialName("synced_ids") val syncedIds: List<String> = emptyList(),
    @SerialName("hermes_ids") val hermesIds: Map<String, String> = emptyMap()
)
