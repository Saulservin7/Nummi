package com.servin.nummi.presentation.home

import com.servin.nummi.domain.model.Transaction

data class HomeState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
