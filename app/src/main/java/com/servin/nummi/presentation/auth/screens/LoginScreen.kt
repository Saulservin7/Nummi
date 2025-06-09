package com.servin.nummi.presentation.auth.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.servin.nummi.R
import com.servin.nummi.presentation.auth.viewmodel.AuthScreenState
import com.servin.nummi.presentation.auth.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current


    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken!!

            viewModel.onLoginWithGoogle(idToken)
        } catch (e: Exception) {
            Toast.makeText(context, "Error de Google Sign-In: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.si)) // ID de cliente web de tu google-services.json
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, googleSignInOptions) }

    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            Toast.makeText(context, "¡Login exitoso!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.loginHandled() // Notificamos al ViewModel que el evento se consumió
        }
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.errorShown() // Notificamos al ViewModel que el error se consumió
        }
    }

    LoginScreenContent(
        onLoginSuccess = { },
        state = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = { viewModel.onLogin() },
        onNavigateToRegister = onNavigateToRegister,
        onGoogleSignInClick = {
            // 5. Al hacer clic, lanzamos la pantalla de selección de cuenta de Google
            googleAuthLauncher.launch(googleSignInClient.signInIntent)
        }
    )


}


@Composable
fun LoginScreenContent(
    onLoginSuccess: () -> Unit,
    state: AuthScreenState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onGoogleSignInClick: () -> Unit

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
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Button(
                onClick = onLoginClick,
                enabled = !state.isLoading,
            ) {
                Text("Iniciar Sesión")
            }
            Button(onClick = onGoogleSignInClick, enabled = !state.isLoading) {
                // Puedes añadir un ícono de Google aquí también
                Text("Iniciar Sesión con Google")
            }

            Button(
                onClick = onNavigateToRegister,
                enabled = !state.isLoading,
            ) {
                Text("Registrarse")
            }

        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }


}