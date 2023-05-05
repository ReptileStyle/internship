package com.kuzevanov.filemanager.ui.screens.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kuzevanov.filemanager.R
import com.kuzevanov.filemanager.ui.screens.home.HomeScreenEvent
import com.kuzevanov.filemanager.ui.screens.home.component.util.SpecialFolders
import kotlin.math.roundToInt

@Composable
fun UserCommonDataTypeTiles(
    modifier: Modifier = Modifier,
    onEvent: (HomeScreenEvent) -> Unit,
    onLayoutBottomPlaced: (IntOffset) -> Unit
) {
    // TODO: make own data source for resource tiles
    val images = stringResource(R.string.images)
    val videos = stringResource(R.string.videos)
    val music = stringResource(R.string.music)
    val apps = stringResource(R.string.apps)
    val zipFiles = stringResource(R.string.zip_files)
    val documents = stringResource(R.string.document)
    val downloads = stringResource(R.string.downloads)
    val add = stringResource(R.string.add)
    val tiles = remember {
        listOf(
            ResourceTile(images, Icons.Rounded.Image, Color(0xFF673AB7), SpecialFolders.Images),
            ResourceTile(videos, Icons.Rounded.VideoLibrary, Color(0xFFF44336), SpecialFolders.Videos),
            ResourceTile(music, Icons.Rounded.LibraryMusic, Color(0xFFFF9800), SpecialFolders.Music),
            ResourceTile(apps, Icons.Rounded.Apps, Color(0xFF03A9F4), SpecialFolders.Apps),
            ResourceTile(zipFiles, Icons.Rounded.Archive, Color(0xFF838383), SpecialFolders.Zip),
            ResourceTile(documents, Icons.Rounded.ListAlt, Color(0xFF1C70B3), SpecialFolders.Docs),
            ResourceTile(downloads, Icons.Rounded.Download, Color(0xFF009688), SpecialFolders.Downloads),
            ResourceTile(add, Icons.Rounded.Add, Color(0xFF03A9F4), SpecialFolders.AddNew),
        ).chunked(4)
    }

    val currentLayoutPlaceListener by rememberUpdatedState(newValue = onLayoutBottomPlaced)

//    val contentPaddingPx = with(LocalDensity.current) { contentPadding.roundToPx() }

    Column(
        modifier = modifier
            .wrapContentHeight(align = Alignment.Top)
            .onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
                val offset = layoutCoordinates.localToRoot(Offset.Zero)
                currentLayoutPlaceListener.invoke(
                    IntOffset(
                        x = 0,
                        y = offset.y.roundToInt() + layoutCoordinates.size.height// + contentPaddingPx
                    )
                )
            }
    ) {
        tiles.forEachIndexed { index, row ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                row.forEach { tile ->
                    ResourceTile(
                        tile = tile,
                        showDashBorder = tile.title == stringResource(id = R.string.add),
                        onClick = {
                            onEvent(HomeScreenEvent.OnOpenSpecialFolderClick(tile.type))
                        }
                    )
                }
            }
        }

    }
}
