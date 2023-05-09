package com.kuzevanov.filemanager.fileSystem.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hashcode")
data class Hashcode(
    @PrimaryKey val path: String = "",
    val hashcode: String = "",
    val isChanged: Boolean = false,//if this file was changed since last launch
    val depth: Int = path.count { it == '/' },//amount of parent folders
    val hashingLaunchID:Int = 0,//was hashed at this id of app launch
) : Parcelable