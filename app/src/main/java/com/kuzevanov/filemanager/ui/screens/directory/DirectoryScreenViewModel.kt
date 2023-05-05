package com.kuzevanov.filemanager.ui.screens.directory

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.fileSystemImpl.LocalFileSystem
import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.fileSystem.model.DirectoryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DirectoryScreenViewModel @Inject constructor(
    private val fileSystem: LocalFileSystem
) : ViewModel() {
    var state by mutableStateOf(DirectoryScreenState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var path: String = ""
        set(value) {
            field = value
            try {
                val directory =  DirectoryInfo(fileSystem.getEntry(value))
                state = state.copy(directory = directory,files = directory.files)
            } catch (e: Exception) {
                Log.d(TAG, "${e.message}")
            }
        }

    init {
        fileSystem.load()
    }


    fun onEvent(event: DirectoryScreenEvent) {
        when(event){
            is DirectoryScreenEvent.OnFileClick ->{
                openFile(event.file)
            }
            is DirectoryScreenEvent.OnBackButtonPress ->{
                val parent = state.directory?.getParent()
                if(parent != null){
                    path = parent.path
                }else{
                    //todo return to home screen
                }
            }
        }
    }
    private fun openFile(file:DirectoryEntry){
        try {
            Log.d(TAG,file.path)
            path = file.path

        }catch (e:Exception){
            viewModelScope.launch {
                _uiEvent.send(UiEvent.Message(e.message ?: "error"))
            }
        }
    }
}


private const val TAG = "HomeVM"