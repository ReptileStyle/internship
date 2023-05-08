package com.kuzevanov.filemanager.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.LocalFileSystem
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.utils.HashcodeRepository
import com.kuzevanov.filemanager.fileSystem.hashDatabase.HashcodeDAO
import com.kuzevanov.filemanager.fileSystem.hashDatabase.FileSystemDatabase
import com.kuzevanov.filemanager.fileSystem.hashDatabase.RecentFileDAO
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
        context: Context,
        repository: HashcodeRepository,
        recentFileDAO: RecentFileDAO
    ): LocalFileSystem = LocalFileSystem(context,repository,recentFileDAO)

    @Provides
    fun provideContentResolver(
        @ApplicationContext
        context: Context
    ): ContentResolver = context.contentResolver

    @Singleton
    @Provides
    fun provideFileSystemDatabase(@ApplicationContext context: Context): FileSystemDatabase {
        return Room.databaseBuilder(context, FileSystemDatabase::class.java, "hashcode.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideHashcodeDAO(database: FileSystemDatabase): HashcodeDAO {
        return database.hashcodeDao()
    }

    @Singleton
    @Provides
    fun provideRecentFileDAO(database: FileSystemDatabase): RecentFileDAO {
        return database.recentFileDao()
    }

    @Singleton
    @Provides
    fun provideHashcodeRepository(
        @ApplicationContext context: Context,
        dao:HashcodeDAO,
        recentFileDAO: RecentFileDAO):HashcodeRepository{
        return HashcodeRepository(context, dao,recentFileDAO)
    }
}