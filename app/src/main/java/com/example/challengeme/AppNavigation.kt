package com.example.challengeme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.challengeme.screens.DashboardScreen
import com.example.challengeme.screens.DailyChallengeScreen
import com.example.challengeme.screens.RandomChallengeScreen
import com.example.challengeme.screens.SettingsScreen
import com.example.challengeme.screens.StreakScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("dashboard/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            DashboardScreen(navController = navController, userId = userId)
        }

        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            ProfileScreen(navController = navController, userId = userId)
        }

        composable("daily") {
            DailyChallengeScreen()
        }

        composable("random") {
            RandomChallengeScreen()
        }

        composable("streak/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            StreakScreen(userId = userId)
        }

        composable("settings") {
            SettingsScreen()
        }
    }
}
