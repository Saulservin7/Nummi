// Ruta: com.servin.nummi.presentation.splash.SplashScreen.kt
package com.servin.nummi.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.servin.nummi.presentation.auth.viewmodel.AuthViewModel
import com.servin.nummi.presentation.navigation.AppScreens

@Composable
fun SplashScreen(
    navController: NavController,
    // Obtenemos la instancia del AuthViewModel a través de Hilt.
    // Hilt se encargará de proveer la misma instancia si ya existe o crear una nueva.
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Observamos el StateFlow 'isUserAuthenticated' del ViewModel.
    // 'collectAsState' convierte el Flow en un State, lo que recompone la vista cuando el valor cambia.
    val isUserAuthenticated by authViewModel.isUserAuthenticated.collectAsState()

    // LaunchedEffect se usa para ejecutar lógica suspendida (como navegación) de forma segura en el ciclo de vida de un Composable.
    // Se ejecutará cada vez que 'isUserAuthenticated' cambie de valor.
    LaunchedEffect(key1 = isUserAuthenticated) {
        // El estado inicial de 'isUserAuthenticated' es 'null'. Esperamos hasta que tenga un valor booleano.
        if (isUserAuthenticated != null) {
            // Si 'isUserAuthenticated' es true, el usuario está logueado.
            if (isUserAuthenticated == true) {
                // Navegamos a la pantalla principal (HomeScreen).
                navController.navigate(AppScreens.HomeScreen.route) {
                    // 'popUpTo(AppScreens.SplashScreen.route) { inclusive = true }' limpia el backstack.
                    // Esto previene que el usuario pueda volver a la SplashScreen presionando el botón de retroceso.
                    popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                }
            } else {
                // Si 'isUserAuthenticated' es false, el usuario no está logueado.
                // Navegamos a la pantalla de Login.
                navController.navigate(AppScreens.LoginScreen.route) {
                    // También limpiamos el backstack aquí por la misma razón.
                    popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                }
            }
        }
    }

    // Aquí puedes mantener tu UI de la SplashScreen (un logo, un spinner, etc.).
    // Por ahora, la dejaremos simple, ya que la lógica de navegación es lo importante.
    // Por ejemplo:
     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
         CircularProgressIndicator()
     }
}