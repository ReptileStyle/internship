package com.kuzevanov.filemanager.ui.screens.directory

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.LocalFileSystem
import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.fileSystem.model.DirectoryInfo
import com.kuzevanov.filemanager.ui.common.model.BottomBarWhileSelectingFilesEvent
import com.kuzevanov.filemanager.ui.common.model.SortingMode
import com.kuzevanov.filemanager.ui.common.model.SortingOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class DirectoryScreenViewModel @Inject constructor(
    private val fileSystem: LocalFileSystem
) : ViewModel() {
    var state by mutableStateOf(DirectoryScreenState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    var fileCheckingJob: Job? = null


    var path: String = ""
        set(value) {
            field = value
            fileCheckingJob?.cancel()
//            coroutineScopeIO.cancel()
            try {
                coroutineScopeIO.launch {
                    Log.d(TAG,"ioscope")
                    val directory = DirectoryInfo(fileSystem.getEntry(value))
                    state = state.copy(directory = directory)
                    val job1 = coroutineScopeIO.launch {
                        val files = directory.files
                        state = state.copy(
                            files = files.sortedBy { it.name.lowercase() })
                    }
                    fileCheckingJob= coroutineScopeIO.launch {
                        job1.join()//preventing race condition
                        val map = fileSystem.getChangedFileInDir(directory.path).collect{
                            state = state.copy(isModifiedList = state.isModifiedList.plus(it))
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "${e.message}")
            }
        }

    init {
        fileSystem.load()
    }


    fun onEvent(event: DirectoryScreenEvent) {
        when (event) {
            is DirectoryScreenEvent.OnFileClick -> {
                openFile(event.file)
            }
            is DirectoryScreenEvent.OnBackButtonPress -> {
                onBackButtonPress()
            }
            is DirectoryScreenEvent.OnDropdownMenuItemClick -> {
                Log.d(TAG,state.toString())
                val event2 = event.item.event
                if (event2 != null) {
                    if (event2 is SortingMode) {
                        changeSortingMode(event2)
                    }
                    if (event2 is SortingOrder) {
                        changeSortingOrder(event2)
                    }
                }
            }
            is DirectoryScreenEvent.OnSelectFile -> {
                onSelectFile(event.file)
            }
            is DirectoryScreenEvent.OnBottomBarWhileSelectingFilesEvent ->{
                onBottomBarWhileSelectingFilesEvent(event.bottomBarWhileSelectingFilesEvent)
            }
        }
    }

    private fun openFile(file: DirectoryEntry) {
        try {
            Log.d(TAG, file.path)
            if(file.isDirectory) {
                path = file.path
            }else{
                file.open()
            }

        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.Message(e.message ?: "error"))
            }
        }
    }

    private fun changeSortingMode(sortingMode: SortingMode) {
        val isAscending = state.sortingOrder == SortingOrder.Ascending
        when (sortingMode) {
            SortingMode.ByDate -> state = state.copy(
                files = state.files.sortedBy { it.lastModified }.reversed(!isAscending)
            )
            SortingMode.ByName -> state = state.copy(
                files = state.files.sortedBy { it.name.lowercase() }.reversed(!isAscending)
            )
            SortingMode.BySize -> {
                val maxSize =
                    state.files.filter { !it.isDirectory }.maxByOrNull { it.size }?.size ?: 1L
                state = state.copy(
                    files = state.files.sortedBy { if (it.isDirectory) it.countChildren.toLong() else -(maxSize - it.size +1) }
                        .reversed(!isAscending)
                )
            }
            SortingMode.ByType -> state = state.copy(
                files = state.files.sortedBy { it.extension.lowercase() }.reversed(!isAscending)
            )
        }
    }

    private fun changeSortingOrder(sortingOrder: SortingOrder) {
        when (sortingOrder) {
            SortingOrder.Ascending -> {
                if (state.sortingOrder != sortingOrder)
                    state =
                        state.copy(sortingOrder = sortingOrder, files = state.files.asReversed())
            }
            SortingOrder.Descending -> {
                if (state.sortingOrder != sortingOrder)
                    state =
                        state.copy(sortingOrder = sortingOrder, files = state.files.asReversed())
            }
        }
    }

    private fun onSelectFile(file: DirectoryEntry){
        state = if (state.selectedFiles.contains(file)){
            state.copy(selectedFiles = state.selectedFiles.minus(file))
        }else{
            state.copy(selectedFiles = state.selectedFiles.plus(file))
        }
    }

    private fun onBackButtonPress(){
        if(state.selectedFiles.isEmpty()) {
            val parent = state.directory?.getParent()
            if (parent?.path != "/storage/emulated" && parent != null) {
                path = parent.path
            } else {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateUp)
                }
            }
        }else{
            state=state.copy(selectedFiles = listOf())
        }
    }

    private fun shareSelectedFiles(){
        try {
            fileSystem.shareFiles(state.selectedFiles.map { it.fileSystemEntry })
            state = state.copy(selectedFiles = listOf())
        }catch (e:Exception){
            viewModelScope.launch {
                _uiEvent.send(UiEvent.Message(e.message ?: "error"))
            }
        }

    }
    private fun onBottomBarWhileSelectingFilesEvent(event:BottomBarWhileSelectingFilesEvent){
        when(event){
            BottomBarWhileSelectingFilesEvent.OnShareFiles -> {
                shareSelectedFiles()
            }
        }
    }

    override fun onCleared() {
        coroutineScopeIO.cancel()
        super.onCleared()
    }
}


fun <T> List<T>.reversed(bool: Boolean = true): List<T> {
    return if (bool)
        this.asReversed()
    else
        this
}


private const val TAG = "HomeVM"