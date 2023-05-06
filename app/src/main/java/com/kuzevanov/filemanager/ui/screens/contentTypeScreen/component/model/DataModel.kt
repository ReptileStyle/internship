package com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.model

import android.net.Uri

sealed class DataModel {
    data class ImageInfo(
        val uri: Uri,
        val size:Long = -1L,
        val name:String = "noname",
        val createdAt:Long = -1L,
    ):DataModel()
}