package com.kuzevanov.filemanager.fileSystem.hashDatabase

import androidx.room.*
import com.kuzevanov.filemanager.fileSystem.model.Hashcode
import kotlinx.coroutines.flow.Flow

@Dao
interface HashcodeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashCode(hashcode: Hashcode)

    @Update
    suspend fun updateHashCode(hashcode: Hashcode)

    @Delete
    suspend fun deleteHashCode(hashcode: Hashcode)

    @Query("SELECT * FROM hashcode WHERE path = :path")
    fun getHashCode(path: String): Flow<Hashcode>

    @Query("SELECT * FROM hashcode WHERE path LIKE :path || '%'")
    fun getAllHashCodesInDir(path:String): Flow<List<Hashcode>>
}