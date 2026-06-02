package com.drivenote.app.data.repository

import com.drivenote.app.data.db.NoteDao
import com.drivenote.app.data.mapper.toDomain
import com.drivenote.app.data.mapper.toEntity
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return noteDao.observeByCategory(category.label).map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.search(query).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getNoteById(noteId: String): Note? {
        return noteDao.findById(noteId)?.toDomain()
    }

    override suspend fun saveNote(note: Note) {
        noteDao.insert(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note.toEntity())
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteById(noteId)
    }

    override suspend fun getUnclassifiedNotes(): List<Note> {
        return noteDao.findUnclassified().map { it.toDomain() }
    }

    override suspend fun getUnsyncedNotes(): List<Note> {
        return noteDao.findUnsynced().map { it.toDomain() }
    }

    override suspend fun markSynced(noteId: String, hermesId: String?) {
        val note = noteDao.findById(noteId) ?: return
        noteDao.update(
            note.copy(
                isSynced = true,
                hermesId = hermesId,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
