package com.kuzevanov.filemanager.domain.fileSystem.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recent")
data class RecentFile(
    @PrimaryKey val path: String = "",
    val date:Long = 0L
) : Parcelable