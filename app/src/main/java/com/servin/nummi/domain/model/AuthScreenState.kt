package com.servin.nummi.domain.model

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
