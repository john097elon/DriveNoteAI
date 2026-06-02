package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(query: String): Flow<List<Note>> = noteRepository.searchNotes(query)
}
