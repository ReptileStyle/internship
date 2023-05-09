package com.kuzevanov.filemanager.navigation

import android.os.Environment
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.kuzevanov.filemanager.fileSystem.model.SpecialFolderTypes
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.ContentTypeScreen
import com.kuzevanov.filemanager.ui.screens.contentTypeScreen.ContentTypeScreenViewModel
import com.kuzevanov.filemanager.ui.screens.directory.DirectoryScreen
import com.kuzevanov.filemanager.ui.screens.directory.DirectoryScreenViewModel
import com.kuzevanov.filemanager.ui.screens.home.HomeScreen
import com.kuzevanov.filemanager.ui.screens.home.HomeScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetupNavGraph(
    navHostController: NavHostController
) {
    AnimatedNavHost(
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
            ),
            enterTransition = {slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)},
            exitTransition = {slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)}
        ) {
            val viewModel: DirectoryScreenViewModel = hiltViewModel()
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = true){
                Log.d("navigation","LaunchedEffectDir")
                coroutineScope.launch {
                    val location = it.arguments?.getString("location") ?: defaultLocation
                    viewModel.setPath(location)
                }
            }
            DirectoryScreen(
                state = viewModel.state,
                onEvent = viewModel::onEvent,
                onNavigate = navHostController::navigate,
                eventFlow = viewModel.uiEvent,
                onNavigateUp = navHostController::navigateUp,
            )
        }
        composable(
            route = Route.home,
            exitTransition = {slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)}
        ){
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
            ),
            enterTransition = {slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)},
            exitTransition = {slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)}
        ){
            val viewModel:ContentTypeScreenViewModel = hiltViewModel()
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = true){
                Log.d("contentType","launchedEffects")
                coroutineScope.launch {
                    val typeNumber = it.arguments!!.getInt("contentType")
                    viewModel.type = SpecialFolderTypes.getTypeFromInt(typeNumber)
                }
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