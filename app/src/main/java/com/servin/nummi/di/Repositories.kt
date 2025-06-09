package com.servin.nummi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class Repositories {
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: com.servin.nummi.data.repository.AuthRepositoryImpl
    ): com.servin.nummi.domain.repository.AuthRepository
}