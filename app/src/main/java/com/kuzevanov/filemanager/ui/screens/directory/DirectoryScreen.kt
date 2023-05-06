package com.kuzevanov.filemanager.ui.screens.directory

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuzevanov.filemanager.core.UiEvent
import com.kuzevanov.filemanager.ui.screens.directory.component.FileComponent
import kotlinx.coroutines.flow.Flow

@Composable
fun DirectoryScreen(
    state: DirectoryScreenState,
    onEvent: (DirectoryScreenEvent) -> Unit,
    eventFlow: Flow<UiEvent>,
    onNavigate: (route: String, popBackStack: Boolean) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true){
        eventFlow.collect{event->
            when(event){
                is UiEvent.Message ->{
                    Toast.makeText(context,event.message,Toast.LENGTH_LONG).show()
                }
                is UiEvent.Navigate->{
                    onNavigate(event.route,event.popBackStack)
                }
                is UiEvent.NavigateUp->{
                    onNavigateUp()
                }
            }
        }
    }
    BackHandler(enabled = true) {
        onEvent(DirectoryScreenEvent.OnBackButtonPress)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.directory?.name ?: "Error directory",
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onSurface,
                        style =  TextStyle(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DirectoryScreenEvent.OnBackButtonPress) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "return to previous directory"
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)){
            items(state.files){file->
                FileComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    file = file,
                    onClick = {onEvent(DirectoryScreenEvent.OnFileClick(file))}
                )
            }
        }
    }


}

