package com.kuzevanov.filemanager.navigation

object Route {
    fun directory(location: String? = null): String {
        return if (location == null) "dir"
        else "dir?location=$location"
    }

    const val home = "home"
}