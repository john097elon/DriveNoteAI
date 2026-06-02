package com.drivenote.app.domain.repository

import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    fun observeNotesByCategory(category: NoteCategory): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun getNoteById(noteId: String): Note?
    suspend fun saveNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String)
    suspend fun getUnclassifiedNotes(): List<Note>
    suspend fun getUnsyncedNotes(): List<Note>
    suspend fun markSynced(noteId: String, hermesId: String?)
}
