package com.kuzevanov.filemanager.fileSystem.hashDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kuzevanov.filemanager.fileSystem.model.Hashcode

@Database(entities = [Hashcode::class], version = 1, exportSchema = false)
abstract class HashcodeDatabase : RoomDatabase() {
    abstract fun hashcodeDao(): HashcodeDAO
}