package com.drivenote.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drivenote.app.domain.repository.NoteRepository
import com.drivenote.app.ml.Classifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ClassificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteRepository: NoteRepository,
    private val classifier: Classifier
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return runCatching {
            val targets = noteRepository.getUnclassifiedNotes()
            targets.forEach { note ->
                val result = classifier.classify(note.text)
                noteRepository.updateNote(
                    note.copy(
                        category = result.category,
                        tags = result.tags,
                        isClassified = true,
                        isSynced = false,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}
