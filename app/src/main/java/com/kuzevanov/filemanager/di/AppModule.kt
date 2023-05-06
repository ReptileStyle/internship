package com.kuzevanov.filemanager.di

import android.content.ContentResolver
import android.content.Context
import com.kuzevanov.filemanager.fileSystem.fileSystemImpl.LocalFileSystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFileSystem(
        @ApplicationContext
        context:Context
    ):LocalFileSystem = LocalFileSystem(context)

    @Provides
    fun provideContentResolver(
        @ApplicationContext
        context:Context
    ):ContentResolver = context.contentResolver
}