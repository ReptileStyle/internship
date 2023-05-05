package com.kuzevanov.filemanager.core

sealed class UiEvent {
    data class Navigate(val route: String, val popBackStack: Boolean = false): UiEvent()
    object NavigateUp: UiEvent()
    data class Message(val message: String?): UiEvent()
}