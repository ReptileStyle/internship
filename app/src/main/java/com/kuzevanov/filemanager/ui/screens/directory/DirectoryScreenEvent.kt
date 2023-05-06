package com.kuzevanov.filemanager.ui.screens.directory



import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.ui.common.model.DropdownMenuTreeNodes

sealed class DirectoryScreenEvent {
    data class OnFileClick(val file:DirectoryEntry):DirectoryScreenEvent()
    object OnBackButtonPress:DirectoryScreenEvent()
    data class OnDropdownMenuItemClick(val item: DropdownMenuTreeNodes):DirectoryScreenEvent()
}