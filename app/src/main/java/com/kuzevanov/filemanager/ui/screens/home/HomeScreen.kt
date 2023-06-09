package com.kuzevanov.filemanager.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlin.math.roundToInt
import com.kuzevanov.filemanager.ui.theme.Shapes
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.ui.common.BottomBarWhileSelectingFiles
import com.kuzevanov.filemanager.ui.screens.home.component.*
import kotlinx.coroutines.flow.Flow
import java.io.File

private enum class RecentFileSheetState { INITIAL, HALF_OPENED, EXPANDED }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit,
    eventFlow: Flow<UiEvent>,
    onNavigate: (route: String, popBackStack: Boolean) -> Unit,
    onNavigateUp: () -> Unit,
) {
    var sheetTopOffset by remember { mutableStateOf(IntOffset.Zero) }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.Message -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is UiEvent.Navigate -> {
                    Log.d("asd", event.route)
                    onNavigate(event.route, event.popBackStack)
                }
                is UiEvent.NavigateUp -> {
                    onNavigateUp()
                }
            }
        }
    }
    val refreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onEvent(HomeScreenEvent.OnRefreshRecentFiles) })
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomBarWhileSelectingFiles(
                isVisible = state.selectedFiles.isNotEmpty(),
                onEvent = { onEvent(HomeScreenEvent.OnBottomBarWhileSelectingFilesEvent(it)) }
            )
        }) {
        Box(modifier = Modifier.pullRefresh(refreshState)) {
            LazyColumn() {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .background(MaterialTheme.colors.background)

                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),

                        ) {

                        SearchBar(
                            modifier = Modifier,
                            onClick = { /* TODO */ }
                        )

                        Spacer(modifier = Modifier.requiredHeight(32.dp))

                        UserStorageCards(
                            state = state,
                            onInternalStorageClick = { onEvent(HomeScreenEvent.OnInternalStorageClick) })

                        Spacer(modifier = Modifier.requiredHeight(32.dp))

                        UserCommonDataTypeTiles(onEvent = onEvent) { layoutPosition ->
                            sheetTopOffset = layoutPosition
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = state.isRefreshing, state = refreshState, modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )
        }
        BackHandler(state.selectedFiles.isNotEmpty()) {
            onEvent(HomeScreenEvent.OnBackClick)
        }

        if (sheetTopOffset != IntOffset.Zero) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp

            val sheetPeekY = sheetTopOffset.y.toFloat()
            val sheetHalfOpenedY = sheetPeekY / 2
            val sheetExpandedY = 0.0f

            val swipeableState =
                rememberSwipeableState(initialValue = RecentFileSheetState.INITIAL)

            val recentSheetSwipeAnchors = mapOf(
                sheetPeekY to RecentFileSheetState.INITIAL,
                sheetHalfOpenedY to RecentFileSheetState.HALF_OPENED,
                sheetExpandedY to RecentFileSheetState.EXPANDED
            )

            val swipingOffset by swipeableState.offset
            val swipeProgress = 1.0f - (swipingOffset / sheetPeekY).coerceIn(0.0f, 1.0f)
            val sheetCornerRadius = lerp(16.dp, 0.dp, swipeProgress)
            Surface(
                shape = Shapes.medium.copy(CornerSize(sheetCornerRadius)),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .requiredSize(screenWidth, screenHeight)
                    .offset { IntOffset(x = 0, y = swipingOffset.roundToInt()) }
                    .swipeable(
                        state = swipeableState,
                        anchors = recentSheetSwipeAnchors,
                        orientation = Orientation.Vertical
                    ),
                elevation = lerp(0.dp, 16.dp, swipeProgress)
            ) {
                Column(
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Recent files",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface
                        )
                    )

                    LazyColumn() {
                        items(state.resentFiles) {
                            RecentFileComponent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                file = File(it.path),
                                onClick = {
                                    it.open()
                                },
                                onLongClick = {
                                    onEvent(HomeScreenEvent.OnSelectFile(it))
                                },
                                isSelected = state.selectedFiles.contains(it),
                                isCheckboxVisible = state.selectedFiles.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }
}



