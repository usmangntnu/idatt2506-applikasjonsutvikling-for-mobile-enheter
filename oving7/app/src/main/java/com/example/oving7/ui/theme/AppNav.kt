package com.example.oving7.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*

import com.example.oving7.viewmodel.FilmViewModel

@Composable
fun AppNav(viewModel: FilmViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen(viewModel) { navController.navigate("settings") }
        }
        composable("settings") {
            SettingsScreen(viewModel) { navController.popBackStack() }
        }
    }
}
