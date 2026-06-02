package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesByCategoryUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(category: NoteCategory): Flow<List<Note>> {
        return noteRepository.observeNotesByCategory(category)
    }
}
