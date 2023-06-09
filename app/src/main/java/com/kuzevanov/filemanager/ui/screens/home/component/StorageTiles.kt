package com.kuzevanov.filemanager.ui.screens.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SdCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kuzevanov.filemanager.ui.theme.MyApplicationTheme
import com.kuzevanov.filemanager.R
import com.kuzevanov.filemanager.ui.screens.home.HomeScreenState
import com.kuzevanov.filemanager.ui.theme.DeviceStorageColor
import com.kuzevanov.filemanager.ui.theme.DropboxColor
import com.kuzevanov.filemanager.ui.theme.GoogleDriveColor
import com.kuzevanov.filemanager.utils.byteFormatter

@Composable
fun UserStorageCards(
    modifier: Modifier = Modifier,
    state:HomeScreenState = HomeScreenState(),
    onInternalStorageClick:()->Unit
) {
    val repositories = remember {
        listOf(
            UserStorage(
                dataProvider = StorageDataProvider.DEVICE,
                bytesUsed = state.usedInternalSpace,
                bytesTotal = state.totalInternalSpace
            ),
            UserStorage(
                dataProvider = StorageDataProvider.GOOGLE_DRIVE,
                bytesUsed = 784,
                bytesTotal = 1024
            ),
            UserStorage(
                dataProvider = StorageDataProvider.DROP_BOX,
                bytesUsed = 256,
                bytesTotal = 1024
            )
        )
    }
    LazyRow(
        modifier = modifier,
    ) {

        itemsIndexed(repositories) { index, repository ->
            val icon = iconForDataProvider(repository.dataProvider)

            val title = storageTitleFor(repository.dataProvider)
            val formattedBytesUsed = byteFormatter(repository.bytesUsed)
            val formattedBytesTotal = byteFormatter(repository.bytesTotal)

            val surfaceColor = colorFor(repository.dataProvider, MaterialTheme.colors.surface)
            val contentAccentColor = MaterialTheme.colors.onSurface

            Row {
                StorageCard(
                    dataIcon = icon,
                    dataIconTintColor = if (repository.dataProvider == StorageDataProvider.DEVICE) Color.White else Color.Unspecified,
                    surfaceColor = surfaceColor,
                    contentAccentColor = contentAccentColor,
                    cardTitle = title,
                    cardText = "$formattedBytesUsed / $formattedBytesTotal",
                    usedSpacePercentage =
                    (repository.bytesUsed / repository.bytesTotal.toFloat()).coerceAtMost(1.0f),
                    onClick = {
                        when(index){
                            0->onInternalStorageClick()
                            else ->{}
                        }
                    }
                )

                if (index < repositories.size) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

@Composable
private fun iconForDataProvider(dataProvider: StorageDataProvider): Painter {
    return when (dataProvider) {
        StorageDataProvider.DEVICE -> rememberVectorPainter(image = Icons.Rounded.SdCard)
        StorageDataProvider.GOOGLE_DRIVE -> painterResource(id = R.drawable.ic_google_drive)
        StorageDataProvider.DROP_BOX -> painterResource(id = R.drawable.ic_dropbox)
    }
}

@Composable
private fun storageTitleFor(dataProvider: StorageDataProvider): String {
    return when (dataProvider) {
        StorageDataProvider.DEVICE -> stringResource(id = R.string.internal_storage)
        StorageDataProvider.GOOGLE_DRIVE -> stringResource(id = R.string.google_drive)
        StorageDataProvider.DROP_BOX -> stringResource(id = R.string.dropbox)
    }
}



@Composable
private fun colorFor(dataProvider: StorageDataProvider, onColor: Color): Color {
    val dataProviderColor = when (dataProvider) {
        StorageDataProvider.DEVICE -> DeviceStorageColor
        StorageDataProvider.GOOGLE_DRIVE -> GoogleDriveColor
        StorageDataProvider.DROP_BOX -> DropboxColor
    }
    return dataProviderColor.copy(alpha = 0.5f).compositeOver(onColor)
}

@Preview
@Composable
private fun UserStoragePreview() {
    MyApplicationTheme() {
        UserStorageCards(onInternalStorageClick = {})
    }
}


enum class StorageDataProvider { DEVICE, GOOGLE_DRIVE, DROP_BOX }
data class UserStorage(
    val dataProvider: StorageDataProvider,
    val bytesUsed: Long,
    val bytesTotal: Long
)
