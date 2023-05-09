package com.kuzevanov.filemanager.ui.screens.directory.component

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileComponent(
    modifier: Modifier = Modifier,
    file:DirectoryEntry,
    onClick:()->Unit,
    onLongClick:()->Unit,
    isModified:Boolean,
    isSelected:Boolean,
    isCheckboxVisible:Boolean
) {
    Row(modifier = modifier
        .padding(vertical = 4.dp, horizontal = 10.dp)
        .combinedClickable(
            onClick = if (isCheckboxVisible) onLongClick else onClick,
            onLongClick = onLongClick
        )) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = file.iconInfo().first,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(),
                tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.85f) else Color.Black.copy(
                    alpha = 0.85f
                )
            )
            if (isModified) {
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
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
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

        AnimatedVisibility(
            visible = isCheckboxVisible,
            enter = fadeIn()+ scaleIn(),
            exit = fadeOut()+ scaleOut()
        ) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(50.dp), contentAlignment = Alignment.Center) {
                Checkbox(
                    checked =isSelected,
                    onCheckedChange ={/*we do nothing, because check will change on row click*/},
                    enabled = false,
                    colors = CheckboxDefaults.colors(
                        disabledUncheckedColor = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                        disabledCheckedColor =MaterialTheme.colors.onBackground.copy(alpha = 0.6f),

                    )
                )
            }
        }


    }
}

