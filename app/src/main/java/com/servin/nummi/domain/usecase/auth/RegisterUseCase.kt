package com.servin.nummi.domain.usecase.auth

import com.servin.nummi.domain.model.User
import com.servin.nummi.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, name: String, password: String): Result<User> {
        return repository.register(email, name, password)
    }
}