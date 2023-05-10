package com.kuzevanov.filemanager.ui.screens.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuzevanov.filemanager.ui.common.RoundedCornerButton
import com.kuzevanov.filemanager.ui.common.dashBorder
import com.kuzevanov.filemanager.ui.extensions.applyIf
import com.kuzevanov.filemanager.ui.screens.home.component.model.ResourceTile



@Composable
fun ResourceTile(
    modifier: Modifier = Modifier,
    tile: ResourceTile,
    showDashBorder: Boolean,
    onClick: (ResourceTile) -> Unit
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerButton(
            modifier = modifier.applyIf(showDashBorder) {
                dashBorder(
                    intervals = { floatArrayOf(10.dp.toPx(), 5.dp.toPx()) },
                    strokeLineWidth = { 1.dp.toPx() },
                    strokeColor = Color.LightGray,
                    cornerRadius = getShapeCornerRadius(tile.backgroundShape)
                )
            },
            icon = tile.icon,
            iconTint = tile.iconTint,
            iconTintAlpha = tile.iconTintAlpha,
            backgroundShape = tile.backgroundShape,
            onClick = { onClick.invoke(tile) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tile.title,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colors.onSurface
        )
    }
}

private fun getShapeCornerRadius(shape: Shape): CornerSize {
    return when (shape) {
        is RoundedCornerShape -> shape.topStart
        else -> CornerSize(0.dp)
    }
}