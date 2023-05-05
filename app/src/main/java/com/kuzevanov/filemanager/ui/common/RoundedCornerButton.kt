package com.kuzevanov.filemanager.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kuzevanov.filemanager.ui.extensions.lighten
import com.kuzevanov.filemanager.ui.theme.MyApplicationTheme

@Composable
fun RoundedCornerButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconTintAlpha: Float,
    backgroundShape: Shape,
    onClick: () -> Unit
) {
    CompositionLocalProvider(
        (LocalContentColor provides iconTint),
        (LocalContentAlpha provides iconTintAlpha)
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier
                .background(
                    color = MaterialTheme.colors.background
                        .copy(alpha = 0.5f)
                        .lighten(0.3f),
                    shape = backgroundShape
                )
                .padding(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}

@Preview(name = "RoundedCornerButton")
@Preview(
    name = "RoundedCornerButtonDark",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun RoundedCornerButtonPreview() {
    MyApplicationTheme() {
        RoundedCornerButton(
            modifier = Modifier.size(64.dp),
            icon = Icons.Rounded.Image,
            iconTint = Color.Magenta,
            iconTintAlpha = if (MaterialTheme.colors.isLight) 1.0f else 0.6f,
            backgroundShape = RectangleShape
        ) { }
    }
}