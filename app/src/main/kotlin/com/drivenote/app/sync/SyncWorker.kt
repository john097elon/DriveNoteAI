package com.drivenote.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drivenote.app.data.mapper.toDto
import com.drivenote.app.data.remote.HermesApiClient
import com.drivenote.app.data.remote.dto.SyncRequestDto
import com.drivenote.app.domain.repository.NoteRepository
import com.drivenote.app.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
    private val hermesApiClient: HermesApiClient
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return runCatching {
            val unsynced = noteRepository.getUnsyncedNotes()
            if (unsynced.isEmpty()) return Result.success()

            val endpoint = settingsRepository.getHermesEndpoint()
            val deviceId = settingsRepository.getDeviceId()
            val request = SyncRequestDto(
                deviceId = deviceId,
                notes = unsynced.map { it.toDto() }
            )

            val response = hermesApiClient.syncNotes(endpoint, request)
            if (!response.success) return Result.retry()

            unsynced.forEach { note ->
                noteRepository.markSynced(
                    noteId = note.id,
                    hermesId = response.hermesIds[note.id]
                )
            }
            settingsRepository.setLastSyncAt(System.currentTimeMillis())
            Result.success()
        }.getOrElse { Result.retry() }
    }
}
