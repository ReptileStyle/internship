package com.kuzevanov.filemanager.navigation

import android.os.Environment
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.ContentTypeScreen
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.ContentTypeScreenViewModel
import com.kuzevanov.filemanager.ui.screens.directory.DirectoryScreen
import com.kuzevanov.filemanager.ui.screens.directory.DirectoryScreenViewModel
import com.kuzevanov.filemanager.ui.screens.home.HomeScreen
import com.kuzevanov.filemanager.ui.screens.home.HomeScreenViewModel

@Composable
fun SetupNavGraph(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Route.home
    ) {
        val defaultLocation = Environment.getExternalStorageDirectory().absolutePath
        composable(
            route = "dir?location={location}",
            arguments = listOf(
                navArgument("location"){
                    this.defaultValue = defaultLocation
                }
            )
        ) {
            val viewModel: DirectoryScreenViewModel = hiltViewModel()
            LaunchedEffect(key1 = true){
                var location =it.arguments?.getString("location") ?: defaultLocation
                viewModel.path=location
                Log.d("nav",viewModel.path)
            }
            DirectoryScreen(
                state = viewModel.state,
                onEvent = viewModel::onEvent,
                onNavigate = navHostController::navigate,
                eventFlow = viewModel.uiEvent,
                onNavigateUp = navHostController::navigateUp,
            )
        }
        composable(route = Route.home){
            val viewModel:HomeScreenViewModel = hiltViewModel()
            HomeScreen(
                state = viewModel.state,
                onEvent = viewModel::onEvent,
                onNavigate = navHostController::navigate,
                eventFlow = viewModel.uiEvent,
                onNavigateUp = navHostController::navigateUp,
            )
        }
        composable(
            route="type?contentType={contentType}",
            arguments = listOf(
                navArgument("contentType"){
                    this.defaultValue = 0
                }
            )
        ){
            val viewModel:ContentTypeScreenViewModel = hiltViewModel()
            LaunchedEffect(key1 = true){
                val typeNumber = it.arguments!!.getInt("contentType")
                viewModel.type = SpecialFolderTypes.getTypeFromInt(typeNumber)
            }
            ContentTypeScreen(
                state = viewModel.state,
                onEvent = viewModel::onEvent,
                onNavigate = navHostController::navigate,
                eventFlow = viewModel.uiEvent,
                onNavigateUp = navHostController::navigateUp,
            )
        }
    }
}

fun NavHostController.navigate(
    route: String,
    popBackStack: Boolean = false
) {
    navigate(route) {
        if (popBackStack) {
            popUpTo(graph.id) {
                inclusive = true
            }
        }
    }
}