package com.kuzevanov.filemanager.ui.screens.home

import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes

sealed class HomeScreenEvent {
    object OnInternalStorageClick:HomeScreenEvent()
    data class OnOpenSpecialFolderClick(val type: SpecialFolderTypes):HomeScreenEvent()
}