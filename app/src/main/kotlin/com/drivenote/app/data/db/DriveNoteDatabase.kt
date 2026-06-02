package com.drivenote.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drivenote.app.data.db.converter.TagsConverter
import com.drivenote.app.data.db.entity.NoteEntity
import com.drivenote.app.data.db.entity.SettingEntity

@Database(
    entities = [NoteEntity::class, SettingEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(TagsConverter::class)
abstract class DriveNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun settingsDao(): SettingsDao
}
