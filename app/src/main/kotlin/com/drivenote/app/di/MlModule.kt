package com.drivenote.app.di

import com.drivenote.app.ml.Classifier
import com.drivenote.app.ml.QwenClassifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MlModule {
    @Binds
    @Singleton
    abstract fun bindClassifier(impl: QwenClassifier): Classifier
}
