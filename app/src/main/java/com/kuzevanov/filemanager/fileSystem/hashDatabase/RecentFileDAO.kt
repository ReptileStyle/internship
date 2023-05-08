package com.kuzevanov.filemanager.fileSystem.hashDatabase

import androidx.room.*
import com.kuzevanov.filemanager.fileSystem.model.RecentFile
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recentFile: RecentFile)

    @Query("SELECT * FROM recent")
    fun getAllRecentFlow(): Flow<List<RecentFile>>

    @Query("DELETE FROM recent WHERE date NOT IN (SELECT date FROM recent ORDER BY date DESC LIMIT 100);")
    fun deleteOutdated()
}