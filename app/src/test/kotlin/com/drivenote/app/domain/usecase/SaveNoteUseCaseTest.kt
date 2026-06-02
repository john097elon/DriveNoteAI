package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SaveNoteUseCaseTest {

    @Test
    fun `note is delegated to repository`() = runBlocking {
        val repository = FakeNoteRepository()
        val useCase = SaveNoteUseCase(repository)
        val note = Note(text = "테스트 메모", category = NoteCategory.TODO)

        useCase(note)

        assertEquals(note.text, repository.saved?.text)
    }

    private class FakeNoteRepository : NoteRepository {
        var saved: Note? = null

        override fun observeNotes(): Flow<List<Note>> = emptyFlow()
        override fun observeNotesByCategory(category: NoteCategory): Flow<List<Note>> = emptyFlow()
        override fun searchNotes(query: String): Flow<List<Note>> = emptyFlow()
        override suspend fun getNoteById(noteId: String): Note? = null
        override suspend fun saveNote(note: Note) {
            saved = note
        }

        override suspend fun updateNote(note: Note) = Unit
        override suspend fun deleteNote(noteId: String) = Unit
        override suspend fun getUnclassifiedNotes(): List<Note> = emptyList()
        override suspend fun getUnsyncedNotes(): List<Note> = emptyList()
        override suspend fun markSynced(noteId: String, hermesId: String?) = Unit
    }
}
