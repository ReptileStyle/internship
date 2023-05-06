package com.kuzevanov.filemanager.ui.screens.contentTypeScreen

import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.model.DataModel

data class ContentTypeScreenState(
    val type: SpecialFolderTypes = SpecialFolderTypes.Downloads,
    val dataList:List<DataModel> = listOf(),
    val isRefreshing: Boolean = false
)