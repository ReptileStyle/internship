package com.kuzevanov.filemanager.ui.screens.directory

import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.fileSystem.model.DirectoryInfo
import com.kuzevanov.filemanager.ui.common.model.SortingMode
import com.kuzevanov.filemanager.ui.common.model.SortingOrder

data class DirectoryScreenState(
    val files: List<DirectoryEntry> = listOf(),
    val selectedFiles: List<DirectoryEntry> = listOf(),
    val directory: DirectoryInfo? = null,
    val sortingOrder: SortingOrder = SortingOrder.Ascending,
    val sortingMode:SortingMode = SortingMode.ByName,
    val isModifiedList:List<String> = listOf()
)