package com.servin.nummi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.servin.nummi.domain.model.User
import com.servin.nummi.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val domainUser = authResult.user?.toDomainModel()
            if (domainUser != null) {
                Result.success(domainUser)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FirebaseUser.toDomainModel(): User {
        return User(
            uid = this.uid,
            email = this.email,
            name = this.displayName,
            isAnonymous = false
        )
    }

    override suspend fun register(email: String, name: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            authResult.user?.updateProfile(profileUpdate)?.await()


            val domainUser = firebaseAuth.currentUser?.toDomainModel()
            if (domainUser != null) {
                Result.success(domainUser)
            } else {
                Result.failure(Exception("No se pudo obtener el usuario después del registro."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toDomainModel()
    }


    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken,null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val domainUser = authResult.user?.toDomainModel()
            if (domainUser!=null) {
                Result.success(domainUser)
            }
            else{
                Result.failure(Exception("Usuario de Google no encontrado "))
            }

        } catch (e:Exception){
            Result.failure(e)
        }
    }
}