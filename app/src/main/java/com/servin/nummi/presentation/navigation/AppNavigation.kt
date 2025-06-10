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
        startDestination = AppScreens.LoginScreen.route
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate(AppScreens.RegisterScreen.route) }
            )
        }


        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(
                onRegistrationSuccess = {
                    navController.navigate(AppScreens.HomeScreen.route) {
                        // Limpiamos la pila también aquí
                        popUpTo(AppScreens.LoginScreen.route) {
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

        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(

            )
        }
    }
}
