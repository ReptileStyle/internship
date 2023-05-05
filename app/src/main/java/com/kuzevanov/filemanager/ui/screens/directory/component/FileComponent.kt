package com.kuzevanov.filemanager.ui.screens.directory.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.utils.iconInfo


@Composable
fun FileComponent(
    modifier: Modifier = Modifier,
    file:DirectoryEntry,
    onClick:()->Unit
) {
    Row(
        modifier = modifier.clickable { onClick() }
    ){
        Icon(file.iconInfo.first,contentDescription = null)
        Column() {
            Text(text = file.name)
            Text(text = file.iconInfo.second)
        }
    }
}