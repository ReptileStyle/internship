package com.kuzevanov.filemanager.ui.screens.home

import com.kuzevanov.filemanager.fileSystem.FileSystemEntry
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.common.model.BottomBarWhileSelectingFilesEvent
sealed class HomeScreenEvent {
    object OnInternalStorageClick:HomeScreenEvent()
    data class OnOpenSpecialFolderClick(val type: SpecialFolderTypes):HomeScreenEvent()
    data class OnSelectFile(val file:FileSystemEntry):HomeScreenEvent()
    object OnBackClick:HomeScreenEvent()
    data class OnBottomBarWhileSelectingFilesEvent(
        val bottomBarWhileSelectingFilesEvent: BottomBarWhileSelectingFilesEvent
    ): HomeScreenEvent()
}