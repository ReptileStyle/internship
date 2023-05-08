package com.kuzevanov.filemanager.fileSystem.hashDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kuzevanov.filemanager.fileSystem.model.Hashcode
import com.kuzevanov.filemanager.fileSystem.model.RecentFile

@Database(entities = [Hashcode::class,RecentFile::class], version = 2, exportSchema = false)
abstract class FileSystemDatabase : RoomDatabase() {
    abstract fun hashcodeDao(): HashcodeDAO
    abstract fun recentFileDao():RecentFileDAO
}