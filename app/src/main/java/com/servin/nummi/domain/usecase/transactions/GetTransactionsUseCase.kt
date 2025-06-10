package com.servin.nummi.domain.usecase.transactions

import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): Flow<List<Transaction>> {
        return repository.getTransactions()
    }
}