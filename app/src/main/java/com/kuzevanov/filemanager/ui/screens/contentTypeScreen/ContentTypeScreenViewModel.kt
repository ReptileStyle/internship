package com.kuzevanov.filemanager.ui.screens.contentTypeScreen

import android.content.ContentResolver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.fileSystemImpl.LocalFileSystem
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.model.DataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ContentTypeScreenViewModel @Inject constructor(
    private val resolver: ContentResolver,
    private val fileSystem: LocalFileSystem
) : ViewModel() {
    var state by mutableStateOf(ContentTypeScreenState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var type: SpecialFolderTypes? = null
        set(value) {
            viewModelScope.launch {
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
        viewModelScope.launch {
            state = state.copy(isRefreshing = true)
            state = state.copy(
                isRefreshing = false,
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
        }
    }
}

private const val TAG = "ContentTypeVM"