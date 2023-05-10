package com.kuzevanov.filemanager.navigation

import com.kuzevanov.filemanager.domain.fileSystem.model.SpecialFolderTypes

object Route {
    fun directory(location: String? = null): String {
        return if (location == null) "dir"
        else "dir?location=$location"
    }
    fun contentType(type: SpecialFolderTypes): String {
        return "type?contentType=${type.toInt()}"
    }

    const val home = "home"
}