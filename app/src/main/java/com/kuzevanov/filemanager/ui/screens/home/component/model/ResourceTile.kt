package com.kuzevanov.filemanager.ui.screens.home.component.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.kuzevanov.filemanager.domain.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.theme.Shapes

data class ResourceTile(
    val title:String,
    val icon: ImageVector,
    val iconTint: Color,
    val type: SpecialFolderTypes,
    val iconTintAlpha:Float = 1.0f,
    val backgroundShape: Shape = Shapes.medium,
)