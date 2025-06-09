package com.servin.nummi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.servin.nummi.presentation.auth.screens.LoginScreen
import com.servin.nummi.presentation.auth.screens.RegisterScreen
import com.servin.nummi.presentation.home.HomeScreen

@Composable

fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.LoginScreen.route
    ) {
        composable(route = AppScreen.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreen.HomeScreen.route) {
                        popUpTo(AppScreen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterScreen.route) }
            )
        }


        composable(route = AppScreen.RegisterScreen.route) {
            RegisterScreen(
                onRegistrationSuccess = {
                    navController.navigate(AppScreen.HomeScreen.route) {
                        // Limpiamos la pila también aquí
                        popUpTo(AppScreen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    // PopBackStack saca la pantalla actual (Registro) de la pila
                    // y nos devuelve a la anterior (Login).
                    navController.popBackStack()
                }
            )
        }

        composable(route = AppScreen.HomeScreen.route) {
            HomeScreen()
        }
    }
}
