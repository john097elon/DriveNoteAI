package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: String) {
        noteRepository.deleteNote(noteId)
    }
}
