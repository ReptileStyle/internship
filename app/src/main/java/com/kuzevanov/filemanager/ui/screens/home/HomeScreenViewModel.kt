package com.kuzevanov.filemanager.ui.screens.home

import android.os.Environment
import android.os.StatFs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.LocalFileSystem
import com.kuzevanov.filemanager.navigation.Route
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val fileSystem: LocalFileSystem
) : ViewModel() {
    var state by mutableStateOf(HomeScreenState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val availableMemory = getAvailableInternalMemorySize()
        val totalMemory = getTotalInternalMemorySize()
        state = state.copy(
            totalInternalSpace = totalMemory,
            usedInternalSpace = totalMemory-availableMemory
        )
        startCollectingRecentFiles()
    }

    fun onEvent(event:HomeScreenEvent){
        when(event){
            is HomeScreenEvent.OnInternalStorageClick->{
                navigateTo(Route.directory())
            }
            is HomeScreenEvent.OnOpenSpecialFolderClick->{
                openSpecialFolder(event.type)
            }
        }
    }

    private fun openSpecialFolder(type: SpecialFolderTypes){
        when(type){
            SpecialFolderTypes.Images->{
//                startActivity("image")
//                Log.d(TAG,fileSystem.getAllByType(type).toString())
                navigateTo(route = Route.contentType(type))
            }
            SpecialFolderTypes.Videos->{
//                Log.d(TAG,fileSystem.getAllByType(type).toString())
            }
            SpecialFolderTypes.Music->{
//                Log.d(TAG,fileSystem.getAllByType(type).toString())
            }
            SpecialFolderTypes.Apps->{

            }
            SpecialFolderTypes.Zip->{

            }
            SpecialFolderTypes.Docs->{

            }
            SpecialFolderTypes.Downloads->{
//                Log.d(TAG,fileSystem.getAllByType(type).toString())
            }
            SpecialFolderTypes.AddNew->{

            }
        }
    }

    private fun navigateTo(route:String,popBackStack:Boolean = false) {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Navigate(route,popBackStack))
        }
    }


    private fun getAvailableInternalMemorySize(): Long {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return availableBlocks*blockSize
    }

    private fun getTotalInternalMemorySize(): Long {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return totalBlocks*blockSize
    }

    private fun startCollectingRecentFiles(){
        viewModelScope.launch {
            fileSystem.getRecentFiles().collectLatest{//drop if not enough time to process
                val list = it.sortedBy { it.date }.map { fileSystem.getEntry(it.path) }
                state = state.copy(resentFiles = list)
                CoroutineScope(Dispatchers.IO).launch{
                    fileSystem.dropOutdatedRecentFiles()
                }
            }
        }
    }
}

private const val TAG = "HomeVM"