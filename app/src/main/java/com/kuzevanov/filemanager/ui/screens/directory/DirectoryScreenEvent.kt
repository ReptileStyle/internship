package com.kuzevanov.filemanager.ui.screens.directory

import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry

sealed class DirectoryScreenEvent {
    data class OnFileClick(val file:DirectoryEntry):DirectoryScreenEvent()
    object OnBackButtonPress:DirectoryScreenEvent()
}