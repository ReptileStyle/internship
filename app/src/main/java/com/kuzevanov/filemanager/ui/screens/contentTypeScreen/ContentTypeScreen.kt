package com.kuzevanov.filemanager.ui.screens.contentTypeScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.component.ContentTypeImagesList
import kotlinx.coroutines.flow.Flow

@Composable
fun ContentTypeScreen(
    state: ContentTypeScreenState,
    onEvent: (ContentTypeScreenEvent) -> Unit,
    eventFlow: Flow<UiEvent>,
    onNavigate: (route: String, popBackStack: Boolean) -> Unit,
    onNavigateUp: () -> Unit,
) {
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
    when (state.type) {
        SpecialFolderTypes.AddNew -> {

        }
        SpecialFolderTypes.Apps -> {

        }
        SpecialFolderTypes.Docs -> {

        }
        SpecialFolderTypes.Downloads -> {

        }
        SpecialFolderTypes.Images -> {
            Log.d("asd","in images")
            ContentTypeImagesList(state = state,onEvent=onEvent)
        }
        SpecialFolderTypes.Music -> {

        }
        SpecialFolderTypes.Videos -> {

        }
        SpecialFolderTypes.Zip -> {

        }
    }

}