package com.kuzevanov.filemanager.ui.screens.contentTypeScreen

import android.content.ContentResolver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.LocalFileSystem.LocalFileSystem
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.model.DataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ContentTypeScreenViewModel @Inject constructor(
    private val fileSystem: LocalFileSystem
) : ViewModel() {
    var state by mutableStateOf(ContentTypeScreenState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val coroutineScopeIO = CoroutineScope(Dispatchers.IO)

    var type: SpecialFolderTypes? = null
        set(value) {
            coroutineScopeIO.launch {
                if (value != null) {
                    field = value
                    state = state.copy(type = value)
                    refreshData()

                }
            }
        }

    init {

    }

    fun onEvent(event: ContentTypeScreenEvent) {
        when (event) {
            is ContentTypeScreenEvent.OnRefresh -> {
                refreshData()
            }
        }
    }

    private fun refreshData() {
        coroutineScopeIO.launch {
            state = state.copy(isRefreshing = true, dataList = listOf())
            state = state.copy(
                dataList = fileSystem.getAllByType(state.type).map {
                    //val file = resolver.openAssetFileDescriptor(it.toUri(),"r")
                    val file = File(it)
                    DataModel.ImageInfo(
                        uri = it.toUri(),
                        name = it.substringAfterLast('/'),
                        size = file.length(),
                        createdAt = file.lastModified()
                    )
                }
            )
            state = state.copy(isRefreshing = false)
        }
    }

    override fun onCleared() {
        coroutineScopeIO.cancel()
        super.onCleared()
    }
}

private const val TAG = "ContentTypeVM"