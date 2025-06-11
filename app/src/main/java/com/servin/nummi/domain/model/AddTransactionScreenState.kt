package com.servin.nummi.domain.model

data class AddTransactionScreenState(
    // Estado de los inputs
    val type: TransactionType= TransactionType.EXPENSE,
    val amount: String = "",
    val description: String = "",
    val category: CategoryType= CategoryType.OTHER,
    val date: String = "",
    val isExpanded: Boolean = false,

    // Estado del resultado de la operación
    val isLoading: Boolean = false,
    val error: String? = null,
    val transactionAdded: Boolean = false
)
