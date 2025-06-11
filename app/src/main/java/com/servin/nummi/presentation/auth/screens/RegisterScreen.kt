package com.servin.nummi.presentation.auth.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.servin.nummi.R
import com.servin.nummi.domain.model.AuthScreenState
import com.servin.nummi.presentation.auth.viewmodel.AuthViewModel
import com.servin.nummi.ui.theme.GreenNummi
import com.servin.nummi.ui.theme.NummiTheme

// =================================================================
// 1. EL CONTENEDOR INTELIGENTE (STATEFUL)
// =================================================================
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit // Lambda para manejar la navegación
) {
    // Recolectamos el estado unificado desde el ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Efecto para manejar el evento de éxito de registro (navegación)
    LaunchedEffect(key1 = uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
            onRegistrationSuccess()
            viewModel.registrationHandled() // Notificamos al ViewModel que el evento se consumió
        }
    }

    // Efecto para manejar los errores
    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.errorShown() // Notificamos al ViewModel que el error se consumió
        }
    }

    // Llamamos al Composable "tonto" que solo se encarga de dibujar
    RegisterScreenContent(
        state = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRegisterClick = viewModel::onRegister
    )
}

// =================================================================
// 2. EL PRESENTADOR TONTO (STATELESS)
// =================================================================
@Composable
fun RegisterScreenContent(
    state: AuthScreenState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp), // Un espaciado consistente es mejor que SpaceAround
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.play_store_512), // Usando tu logo importado
                contentDescription = "Logo de Nummi",
                modifier = Modifier.size(120.dp) // Es bueno darle un tamaño explícito
            )

            // Usamos OutlinedTextField que suele verse un poco más moderno
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Button(
                onClick = onRegisterClick,
                enabled = !state.isLoading, // El botón se deshabilita mientras carga
                // Ya no necesitas especificar el color aquí si lo definiste como 'primary' en tu tema
                // colors = ButtonDefaults.buttonColors(containerColor = GreenNummi),
            ) {
                Text("Registrarse")
            }
        }

        // Mostramos el indicador de carga en el centro de la pantalla si está cargando
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}


// =================================================================
// 3. EL PREVIEW AHORA ES SÚPER FÁCIL
// =================================================================
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    NummiTheme {
        // Llamamos a la versión "tonta", pasándole datos de mentira. ¡No necesita ViewModel!
        RegisterScreenContent(
            state = AuthScreenState(isLoading = false),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Register Screen Loading")
@Composable
fun RegisterScreenLoadingPreview() {
    NummiTheme {
        RegisterScreenContent(
            state = AuthScreenState(isLoading = true), // ¡Podemos previsualizar el estado de carga!
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {}
        )
    }
}