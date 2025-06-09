package com.servin.nummi.domain.usecase.auth

import com.servin.nummi.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String) = repository.signInWithGoogle(idToken)
}