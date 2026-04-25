package dev.saragones3.genogramia.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.saragones3.genogramia.presentation.home.GuestHomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "guest_home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("guest_home") {
            GuestHomeScreen(
                onNavigateToLogin = {
                    // Navigate to Login (US-003)
                    // navController.navigate("login")
                },
                onNavigateToSampleTree = {
                    // Navigate to Sample Tree
                },
                onNavigateToCreateTree = {
                    // Navigate to Create Tree (Guest)
                }
            )
        }
    }
}
