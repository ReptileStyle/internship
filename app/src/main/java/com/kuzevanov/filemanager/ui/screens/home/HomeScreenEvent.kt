package com.kuzevanov.filemanager.ui.screens.home

sealed class HomeScreenEvent {
    object OnInternalStorageClick:HomeScreenEvent()
    data class OnOpenSpecialFolderClick(val type:Int):HomeScreenEvent()
}