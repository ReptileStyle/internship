package com.kuzevanov.filemanager.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuzevanov.filemanager.ui.common.model.BottomBarWhileSelectingFilesEvent

@Composable
fun BottomBarWhileSelectingFiles(
    isVisible:Boolean,
    onEvent:(BottomBarWhileSelectingFilesEvent)->Unit
){
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .height(50.dp),
            horizontalArrangement = Arrangement.Center,

        ) {
            IconButton(onClick = { onEvent(BottomBarWhileSelectingFilesEvent.OnShareFiles) }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share selected files"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onEvent(BottomBarWhileSelectingFilesEvent.OnDeleteFiles) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete selected files"
                )
            }
        }
    }
}