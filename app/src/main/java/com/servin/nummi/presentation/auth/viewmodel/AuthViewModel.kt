package com.servin.nummi.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.usecase.auth.GetCurrentUserUseCase
import com.servin.nummi.domain.usecase.auth.LoginUseCase
import com.servin.nummi.domain.usecase.auth.RegisterUseCase
import com.servin.nummi.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Unimos todo el estado de la pantalla en una sola clase.
data class AuthScreenState(
    // Estado de los inputs
    val name: String = "",
    val email: String = "",
    val password: String = "",

    // Estado del resultado de la operación
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val loginSuccess: Boolean = false,
    val authenticationSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow(AuthScreenState())
    val uiState = _uiState.asStateFlow() // La UI observará este único estado

    private val _isUserAuthenticated = MutableStateFlow<Boolean?>(null)

    // isUserAuthenticated es la versión pública y de solo lectura del StateFlow.
    // La UI observará este Flow para reaccionar a los cambios de estado de autenticación.
    val isUserAuthenticated: StateFlow<Boolean?> = _isUserAuthenticated.asStateFlow()


    init {
        getCurrentUser()
        viewModelScope.launch {
            // Se invoca el caso de uso para obtener el usuario actual.
            // Esto es una llamada única que devuelve el usuario de Firebase si existe una sesión activa.
            val currentUser = getCurrentUserUseCase()

            // Actualizamos el valor de nuestro StateFlow.
            // Si currentUser no es nulo, significa que el usuario está logueado.
            _isUserAuthenticated.value = currentUser != null
        }
    }

    // 2. Las funciones de cambio ahora actualizan el estado unificado.
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    private fun getCurrentUser() {
        val user = getCurrentUserUseCase()
        if (user != null && !user.isAnonymous) {
            _uiState.update {
                it.copy(
                    authenticationSuccess = true,
                    name = user.name ?: "",
                    email = user.email ?: ""
                )
            }
        }
    }

    fun onRegister() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = registerUseCase(
                email = currentState.email,
                name = currentState.name,
                password = currentState.password
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccess = true,
                            name = user.name ?: "",
                            email = user.email ?: ""
                        )
                    }
                },
                onFailure = { exception -> // <-- BUG CORREGIDO
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
            )
        }
    }

    fun onLogin() {
        val currentState = _uiState.value

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = loginUseCase(
                email = currentState.email,
                password = currentState.password
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            name = user.name ?: "",
                            email = user.email ?: ""
                        )
                    }

                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
            )
        }
    }

    fun onLoginWithGoogle(idToken: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = signInWithGoogleUseCase(idToken)

            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            authenticationSuccess = true,
                            name = user.name ?: "",
                            email = user.email ?: ""
                        )
                    }
                }, onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
            )
        }

    }

    // 3. Funciones para que la UI notifique que ya manejó el evento de error o éxito.
    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun registrationHandled() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }

    fun loginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}