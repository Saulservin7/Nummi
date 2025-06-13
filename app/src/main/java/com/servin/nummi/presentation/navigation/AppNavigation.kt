// Ruta: com.servin.nummi.presentation.navigation/AppNavigation.kt
package com.servin.nummi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.servin.nummi.presentation.auth.screens.LoginScreen
import com.servin.nummi.presentation.auth.screens.RegisterScreen
import com.servin.nummi.presentation.home.HomeScreen
import com.servin.nummi.presentation.home.features
import com.servin.nummi.presentation.saving.SavingGoalsScreen
import com.servin.nummi.presentation.splash.SplashScreen // 1. Importamos nuestra nueva SplashScreen.
import com.servin.nummi.presentation.transaction.AddTransactionScreen
import com.servin.nummi.presentation.transactionlist.TransactionListScreen

@Composable
fun AppNavigation() {
    // El NavController sigue siendo el gestor de la navegación.
    val navController = rememberNavController()

    // NavHost es el contenedor que aloja los diferentes destinos (pantallas).
    NavHost(
        navController = navController,
        // 2. CAMBIO CLAVE: La ruta de inicio ahora es la SplashScreen.
        // La app siempre comenzará aquí.
        startDestination = AppScreens.SplashScreen.route
    ) {
        // 3. AÑADIMOS LA RUTA PARA LA SPLASHSCREEN:
        // Este es el nuevo punto de entrada en nuestro grafo de navegación.
        composable(route = AppScreens.SplashScreen.route) {
            // Le pasamos el navController a la SplashScreen para que pueda
            // redirigir al usuario una vez que se verifique la sesión.
            SplashScreen(navController = navController)
        }

        // El resto de las rutas permanecen igual, pero ahora la SplashScreen
        // es la que decide cuándo navegar hacia ellas.
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    // La lógica de navegación tras un login exitoso sigue siendo la misma.
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
                        popUpTo(AppScreens.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(
                onFeatureClick = { route ->
                    navController.navigate(route)

                }
            )
        }
        composable(route = AppScreens.AddTransactionScreen.route) {
            AddTransactionScreen()
        }
        composable(route = AppScreens.TransactionListScreen.route) {
            TransactionListScreen()
        }
        composable(route = AppScreens.SavingsGoalsScreen.route) {
            SavingGoalsScreen()
        }
    }
}