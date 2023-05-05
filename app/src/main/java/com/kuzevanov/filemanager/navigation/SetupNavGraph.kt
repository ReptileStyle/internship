package com.kuzevanov.filemanager.navigation

import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kuzevanov.filemanager.fileSystem.filenavigation.Location
import com.kuzevanov.filemanager.fileSystem.filenavigation.LocationType
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
        val defaultLocation = Location(
            path = Environment.getExternalStorageDirectory().absolutePath
        )
        Log.d("nav",defaultLocation.path)
        composable(
            route = "dir?location={location}",
            arguments = listOf(
                navArgument("location"){
                    this.type= LocationType()
                    this.defaultValue = defaultLocation
                }
            )
        ) {
            val viewModel: DirectoryScreenViewModel = hiltViewModel()
            LaunchedEffect(key1 = true){
                var location: Location = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    it.arguments!!.getParcelable("location", Location::class.java)!!
                }else{
                    it.arguments!!.getParcelable<Location>("location")!!
                }
                viewModel.path=location.path
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