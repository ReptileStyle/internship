package com.kuzevanov.filemanager.ui.screens.home

import com.kuzevanov.filemanager.fileSystem.FileSystemEntry

data class HomeScreenState(
    val totalInternalSpace:Long = -1L,
    val usedInternalSpace:Long = -1L,
    val resentFiles: List<FileSystemEntry> = listOf()
)