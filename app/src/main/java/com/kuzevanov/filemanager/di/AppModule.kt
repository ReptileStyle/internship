package com.kuzevanov.filemanager.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.LocalFileSystem
import com.kuzevanov.filemanager.fileSystem.hashDatabase.HashcodeDAO
import com.kuzevanov.filemanager.fileSystem.hashDatabase.HashcodeDatabase
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
        context: Context
    ): LocalFileSystem = LocalFileSystem(context)

    @Provides
    fun provideContentResolver(
        @ApplicationContext
        context: Context
    ): ContentResolver = context.contentResolver

    @Singleton
    @Provides
    fun provideHashcodeDatabase(@ApplicationContext context: Context): HashcodeDatabase {
        return Room.databaseBuilder(context, HashcodeDatabase::class.java, "hashcode.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideHashcodeDAO(database: HashcodeDatabase): HashcodeDAO {
        return database.hashcodeDao()
    }
}