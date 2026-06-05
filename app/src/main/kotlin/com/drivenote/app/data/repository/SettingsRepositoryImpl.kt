package com.drivenote.app.data.repository

import com.drivenote.app.data.db.SettingsDao
import com.drivenote.app.data.db.entity.SettingEntity
import com.drivenote.app.domain.repository.SettingsRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override suspend fun getHermesEndpoint(): String {
        return settingsDao.getValue(KEY_HERMES_ENDPOINT) ?: DEFAULT_ENDPOINT
    }

    override suspend fun setHermesEndpoint(endpoint: String) {
        settingsDao.upsert(
            SettingEntity(
                key = KEY_HERMES_ENDPOINT,
                value = endpoint.trim(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getHermesAuthToken(): String {
        return settingsDao.getValue(KEY_HERMES_AUTH_TOKEN).orEmpty()
    }

    override suspend fun setHermesAuthToken(authToken: String) {
        settingsDao.upsert(
            SettingEntity(
                key = KEY_HERMES_AUTH_TOKEN,
                value = authToken.trim(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getDeviceId(): String {
        val existing = settingsDao.getValue(KEY_DEVICE_ID)
        if (existing != null) return existing
        val created = UUID.randomUUID().toString()
        settingsDao.upsert(
            SettingEntity(
                key = KEY_DEVICE_ID,
                value = created,
                updatedAt = System.currentTimeMillis()
            )
        )
        return created
    }

    override suspend fun getLastSyncAt(): Long? {
        return settingsDao.getValue(KEY_LAST_SYNC_AT)?.toLongOrNull()
    }

    override suspend fun setLastSyncAt(timestamp: Long) {
        settingsDao.upsert(
            SettingEntity(
                key = KEY_LAST_SYNC_AT,
                value = timestamp.toString(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private companion object {
        const val KEY_HERMES_ENDPOINT = "hermes_endpoint"
        const val KEY_HERMES_AUTH_TOKEN = "hermes_auth_token"
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_LAST_SYNC_AT = "last_sync_at"
        const val DEFAULT_ENDPOINT = "https://hermes.local"
    }
}
