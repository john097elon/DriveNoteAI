package com.drivenote.app.di

import android.content.Context
import androidx.room.Room
import com.drivenote.app.data.db.DriveNoteDatabase
import com.drivenote.app.data.db.NoteDao
import com.drivenote.app.data.db.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DriveNoteDatabase {
        return Room.databaseBuilder(
            context,
            DriveNoteDatabase::class.java,
            "drivenote.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: DriveNoteDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideSettingsDao(database: DriveNoteDatabase): SettingsDao = database.settingsDao()
}
