package com.servin.nummi.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.servin.nummi.data.repository.AuthRepositoryImpl
import com.servin.nummi.data.repository.FinancialRepositoryImpl
import com.servin.nummi.domain.repository.AuthRepository
import com.servin.nummi.domain.repository.FinancialRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// El módulo sigue siendo una 'abstract class' para poder usar @Binds.
@Module
@InstallIn(SingletonComponent::class)
abstract class Repositories {

    // El método con @Binds se queda exactamente como está.
    // Es abstracto y le dice a Hilt que use AuthRepositoryImpl para AuthRepository.
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // CORRECCIÓN: Movemos la función @Provides a un companion object.
    // Esto hace que la función sea estática, cumpliendo la regla de Hilt de que un módulo
    // no puede tener métodos de instancia y abstractos al mismo tiempo.
    companion object {

        // Esta función ahora puede convivir con @Binds en el mismo archivo.
        // La hemos refactorizado para que no vincule, sino que provea directamente,
        // ya que FinancialRepositoryImpl necesita dependencias (auth, firestore).
        @Singleton
        @Provides
        fun provideFinancialRepository(
            auth: FirebaseAuth,
            firestore: FirebaseFirestore
        ): FinancialRepository = FinancialRepositoryImpl(
            auth = auth,
            firestore = firestore
        )
    }
}