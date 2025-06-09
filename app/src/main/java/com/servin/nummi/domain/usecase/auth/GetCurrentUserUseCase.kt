package com.servin.nummi.domain.usecase.auth

import com.servin.nummi.domain.model.User
import com.servin.nummi.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val repository: AuthRepository) {

    operator fun invoke(): User?{
        return repository.getCurrentUser()

    }
}