package com.servin.nummi.di

import com.servin.nummi.domain.repository.FinancialRepository
import com.servin.nummi.domain.usecase.transactions.AddTransactionUseCase
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

// Usamos ViewModelComponent porque los casos de uso generalmente tienen el mismo ciclo de vida
// que los ViewModels que los usan. Podríamos usar SingletonComponent también.
@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {

    // Provee una instancia de GetTransactionsUseCase.
    // Hilt sabe cómo proveer FinancialRepository gracias al módulo de Repositories.
    @Provides
    fun provideGetTransactionsUseCase(repository: FinancialRepository): GetTransactionsUseCase {
        return GetTransactionsUseCase(repository)
    }

    // Provee una instancia de AddTransactionUseCase.
    @Provides
    fun provideAddTransactionUseCase(repository: FinancialRepository): AddTransactionUseCase {
        return AddTransactionUseCase(repository)
    }
}