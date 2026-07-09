package com.example.spacexhero.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "launches"
    ) {
        composable("launches") {
            LaunchesScreen(
                onNavigateToDetails = { launchId ->
                    navController.navigate("launches/$launchId")
                }
            )
        }

        composable("launches/{launchId}") { backStackEntry ->
            val launchId = backStackEntry.arguments?.getString("launchId") ?: ""
            LaunchDetailsScreen(
                launchId = launchId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
