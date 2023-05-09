package com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.ContentTypeScreenEvent
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.ContentTypeScreenState
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.model.DataModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentTypeImagesList(
    state: ContentTypeScreenState,
    onEvent: (ContentTypeScreenEvent) -> Unit
) {
    val refreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onEvent(ContentTypeScreenEvent.OnRefresh) })
    Box(modifier = Modifier.pullRefresh(refreshState).background(color = MaterialTheme.colors.background)){
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.dataList as List<DataModel.ImageInfo>){dataModel ->
                ImageTile(
                    modifier = Modifier.fillMaxWidth().height(70.dp),
                    imageInfo = dataModel,
                )
            }
        }
        PullRefreshIndicator(refreshing = state.isRefreshing, state = refreshState, modifier = Modifier.align(
            Alignment.TopCenter))
    }
}