package com.example.forestry.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forestry.viewmodel.ForestryViewModel
import com.example.forestry.ui.screens.LoginScreen
import com.example.forestry.ui.screens.ProjectListScreen
import com.example.forestry.ui.theme.MapScreen
import com.example.forestry.ui.screens.ConfigurationScreen
import com.example.forestry.ui.screens.NewTreeScreen
import com.example.forestry.ui.screens.PreferencesScreen

@Composable
fun Navigation(viewModel: ForestryViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is NavEvent.Navigate -> {
                    navController.navigate(event.route) {
                        if (event.popBackStack) popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                NavEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.LOGIN.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) },
    ) {
        composable(
            Screen.LOGIN.route,
            enterTransition = { fadeIn(tween(700)) },
            exitTransition = { fadeOut(tween(700)) },
            popEnterTransition = { fadeIn(tween(700)) },
            popExitTransition = { fadeOut(tween(700)) },
        ) {
            LoginScreen(viewModel)
        }

        composable(
            Screen.MAP.route,
            enterTransition = { fadeIn(tween(700)) },
            popEnterTransition = { EnterTransition.None },
        ) {
            MapScreen(viewModel)
        }

        composable(Screen.CONFIGURATION.route) {
            ConfigurationScreen(viewModel)
        }

        composable(Screen.PROJECTS.route) {
            ProjectListScreen(viewModel)
        }

        composable(Screen.PREFERENCES.route) {
            PreferencesScreen(viewModel)
        }

        composable(Screen.NEWTREE.route) {
            NewTreeScreen(viewModel)
        }
    }
}
