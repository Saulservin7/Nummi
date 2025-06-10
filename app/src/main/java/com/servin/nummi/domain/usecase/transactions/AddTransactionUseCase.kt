package com.servin.nummi.domain.usecase.transactions

import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(transaction: Transaction) = repository.addTransaction(transaction)

}