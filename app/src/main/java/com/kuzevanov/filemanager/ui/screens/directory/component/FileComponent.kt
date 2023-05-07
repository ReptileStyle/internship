package com.kuzevanov.filemanager.ui.screens.directory.component

import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuzevanov.filemanager.fileSystem.model.DirectoryEntry
import com.kuzevanov.filemanager.utils.byteFormatter
import com.kuzevanov.filemanager.utils.getDate
import com.kuzevanov.filemanager.utils.iconInfo


@Composable
fun FileComponent(
    modifier: Modifier = Modifier,
    file:DirectoryEntry,
    onClick:()->Unit
) {
    Row(modifier = modifier
        .padding(vertical = 4.dp, horizontal = 10.dp)
        .clickable {
            onClick()
        }) {
        Box(contentAlignment = Alignment.Center) {
            var isModifiedState by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = file) {
                isModifiedState = file.isModified()
            }
            Icon(
                imageVector = file.iconInfo().first,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(),
                tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.85f) else Color.Black.copy(
                    alpha = 0.85f
                )
            )
            if (file.isModified()) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(12.dp)
                        .align(Alignment.TopEnd),
                    tint = Color.Red.copy(alpha = 0.75f)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            androidx.compose.material.Text(
                text = file.name,
                maxLines = 2,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.9f) else Color.Black.copy(
                        alpha = 0.9f
                    )
                )
            )
            androidx.compose.material.Text(
                text = "${
                    if (file.isDirectory) "${file.countChildren} files" else byteFormatter(bytes = file.size)
                } | ${
                    getDate(
                        file.lastModified,
                        "dd/MM/yyyy hh:mm:ss"
                    )
                }",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.75f) else Color.Black.copy(
                        alpha = 0.75f
                    )
                )
            )
        }

    }
}

