package com.servin.nummi.domain.usecase.auth

import com.servin.nummi.domain.model.User
import com.servin.nummi.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String
        // ¡Cambiamos el tipo de retorno aquí también!
    ): Result<User> {
        // Aquí puedes añadir lógica de negocio, como validaciones
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("El correo y la contraseña no pueden estar vacíos."))
        }
        return repository.login(email, password)
    }
}