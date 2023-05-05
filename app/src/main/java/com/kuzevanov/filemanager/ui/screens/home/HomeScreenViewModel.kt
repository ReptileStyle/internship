package com.kuzevanov.filemanager.ui.screens.home

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.fileSystemImpl.LocalFileSystem
import com.kuzevanov.filemanager.navigation.Route
import com.kuzevanov.filemanager.ui.screens.home.component.util.MediaFinderHelper
import com.kuzevanov.filemanager.ui.screens.home.component.util.SpecialFolders
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val fileSystem: LocalFileSystem
) : ViewModel() {
    var state by mutableStateOf(HomeScreenState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val mediaFinderHelper = MediaFinderHelper(context.contentResolver)

    init {
        val availableMemory = getAvailableInternalMemorySize()
        val totalMemory = getTotalInternalMemorySize()
        state = state.copy(
            totalInternalSpace = totalMemory,
            usedInternalSpace = totalMemory-availableMemory
        )
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

    private fun openSpecialFolder(type:Int){
        when(type){
            SpecialFolders.Images->{
//                startActivity("image")
                Log.d(TAG,mediaFinderHelper.getAllByType(type).toString())
            }
            SpecialFolders.Videos->{
                Log.d(TAG,mediaFinderHelper.getAllByType(type).toString())
            }
            SpecialFolders.Music->{
                Log.d(TAG,mediaFinderHelper.getAllByType(type).toString())
            }
            SpecialFolders.Apps->{

            }
            SpecialFolders.Zip->{

            }
            SpecialFolders.Docs->{

            }
            SpecialFolders.Downloads->{
                Log.d(TAG,mediaFinderHelper.getAllByType(type).toString())
            }
            SpecialFolders.AddNew->{

            }
        }
    }

    private fun startActivity(type:String){
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.type = "$type/*"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }catch (e:Exception){
            Log.d(TAG,e.message?:"error to launch activity")
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
}

private const val TAG = "HomeVM"