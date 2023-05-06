package com.kuzevanov.filemanager.fileSystem.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hashcode")
data class Hashcode(
    val path: String = "",
    val hashcode:String = ""
):Parcelable