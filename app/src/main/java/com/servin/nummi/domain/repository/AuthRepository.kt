package com.servin.nummi.domain.repository

import com.servin.nummi.domain.model.User

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String,name:String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    fun getCurrentUser(): User?
    suspend fun signInWithGoogle(idToken:String):Result<User>
}