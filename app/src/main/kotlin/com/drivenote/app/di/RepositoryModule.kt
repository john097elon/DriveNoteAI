package com.drivenote.app.di

import com.drivenote.app.data.repository.NoteRepositoryImpl
import com.drivenote.app.data.repository.SettingsRepositoryImpl
import com.drivenote.app.domain.repository.NoteRepository
import com.drivenote.app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
