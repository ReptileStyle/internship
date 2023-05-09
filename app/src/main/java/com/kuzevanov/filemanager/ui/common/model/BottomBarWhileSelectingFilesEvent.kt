package com.kuzevanov.filemanager.ui.common.model

sealed class BottomBarWhileSelectingFilesEvent{
    object OnShareFiles:BottomBarWhileSelectingFilesEvent()
    object OnDeleteFiles:BottomBarWhileSelectingFilesEvent()
}
