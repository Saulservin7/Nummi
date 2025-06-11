package com.servin.nummi.domain.model


data class TransactionListScreenState(
    val transaction: List<Transaction> = emptyList(),

    // Estado del resultado de la operación
    val isLoading: Boolean = false,
    val error: String? = null,
    val transactionAdded: Boolean = false

)