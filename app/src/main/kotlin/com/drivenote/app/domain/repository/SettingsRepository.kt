package com.drivenote.app.domain.repository

interface SettingsRepository {
    suspend fun getHermesEndpoint(): String
    suspend fun setHermesEndpoint(endpoint: String)
    suspend fun getDeviceId(): String
    suspend fun getLastSyncAt(): Long?
    suspend fun setLastSyncAt(timestamp: Long)
}
