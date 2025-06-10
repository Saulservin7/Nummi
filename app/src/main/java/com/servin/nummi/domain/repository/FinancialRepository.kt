package com.servin.nummi.domain.repository

import com.servin.nummi.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface FinancialRepository {

    suspend fun getTransactions(): Flow<List<Transaction>>

    suspend fun addTransaction(transaction: Transaction)
}