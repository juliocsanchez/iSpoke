package com.example.ispoke.android.navigation

import Practice
import SharedViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ispoke.android.classes.GestureCheck
import view.Register
import view.Gesture
import view.Home
import view.Login
import view.ModuleScreen
import view.Profile

@Composable
fun Routes() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()
    val gestureCheck: GestureCheck = viewModel()


    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("home") {
            Home(navController, sharedViewModel, gestureCheck )
        }
        composable(
            route = "module/{title}/{imageResId}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("imageResId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            ModuleScreen(
                navController = navController,
                title = backStackEntry.arguments?.getString("title") ?: "",
                imageResId = backStackEntry.arguments?.getInt("imageResId") ?: 0
            )
        }
        composable(
            route = "gesture/{letter}/{imageResId}/{title}",
            arguments = listOf(
                navArgument("letter") { type = NavType.StringType },
                navArgument("imageResId") { type = NavType.IntType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            Gesture(
                navController = navController,
                imageResId = backStackEntry.arguments?.getInt("imageResId") ?: 0,
                title = backStackEntry.arguments?.getString("title") ?: "",
                letter = backStackEntry.arguments?.getString("letter") ?: ""
            )
        }
        composable(
            route = "practice/{letter}",
            arguments = listOf(
                navArgument("letter") { type = NavType.StringType },
            )
        ) {
            backStackEntry ->
            Practice(navController = navController, letter = backStackEntry.arguments?.getString("letter") ?: "" )
        }
        composable("profile") {
            Profile(navController)
        }
        composable("login") {
            Login(navController)
        }
        composable("register") {
            Register(navController)
        }
    }
}
