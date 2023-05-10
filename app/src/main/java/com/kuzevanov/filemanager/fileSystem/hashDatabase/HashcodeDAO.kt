package com.kuzevanov.filemanager.fileSystem.hashDatabase

import androidx.room.*
import com.kuzevanov.filemanager.domain.fileSystem.model.Hashcode

@Dao
interface HashcodeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashcode(hashcode: Hashcode)

    @Update
    suspend fun updateHashcode(hashcode: Hashcode)

    @Delete
    suspend fun deleteHashcode(hashcode: Hashcode)

    @Query("SELECT * FROM hashcode WHERE path = :path LIMIT 1")
    suspend fun getHashcode(path: String): Hashcode?

    @Query("SELECT * FROM hashcode WHERE depth = :depth AND path LIKE :path || '%'")
    fun getAllHashcodesInDir(path:String, depth:Int = path.count { it=='/' } + 1): List<Hashcode>
}