package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.repository.NoteRepository
import javax.inject.Inject

class GetUnsyncedNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(): List<Note> = noteRepository.getUnsyncedNotes()
}
