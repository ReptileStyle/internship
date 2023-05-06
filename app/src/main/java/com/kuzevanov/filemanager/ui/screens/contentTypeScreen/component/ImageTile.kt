package com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.model.DataModel
import com.kuzevanov.filemanager.utils.byteFormatter
import com.kuzevanov.filemanager.utils.getDate

@Composable
fun ImageTile(
    modifier: Modifier=Modifier,
    imageInfo: DataModel.ImageInfo
) {
    Row(modifier = modifier.padding(vertical = 4.dp, horizontal = 10.dp)){
        Image(
            painter = rememberAsyncImagePainter(imageInfo.uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            Text(
                text = imageInfo.name,
                maxLines = 2,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.9f)
                )
            )
            Text(
                text = "${byteFormatter(bytes = imageInfo.size)} | ${getDate(imageInfo.createdAt,"dd/MM/yyyy hh:mm:ss")}",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.75f) else Color.Black.copy(alpha = 0.75f)
                )
            )
        }
    }
}