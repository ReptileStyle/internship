package com.kuzevanov.filemanager.ui.screens.directory

import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.fileSystem.model.DirectoryInfo

data class DirectoryScreenState(
    val files: List<DirectoryEntry> = listOf(),
    val selectedFiles: List<DirectoryEntry> = listOf(),
    val directory: DirectoryInfo? = null
)