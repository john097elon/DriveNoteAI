package com.drivenote.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.drivenote.app.data.db.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY created_at DESC")
    fun observeByCategory(category: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE text LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun search(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_classified = 0")
    suspend fun findUnclassified(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE is_synced = 0")
    suspend fun findUnsynced(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun findById(id: String): NoteEntity?
}
