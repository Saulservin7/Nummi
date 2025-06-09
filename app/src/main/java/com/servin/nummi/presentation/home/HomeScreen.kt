package com.servin.nummi.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.servin.nummi.presentation.auth.viewmodel.AuthViewModel
import com.servin.nummi.presentation.navigation.AppScreen

@Composable
fun HomeScreen(
    // Podemos usar el mismo AuthViewModel para obtener la info del usuario
    // aunque lo ideal en el futuro sería un HomeViewModel.
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Volvemos a recolectar el estado para tener la info más reciente
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Mostramos el nombre si no es nulo, o un saludo genérico
        Text("¡Bienvenido, ${uiState.name}!")
    }
}