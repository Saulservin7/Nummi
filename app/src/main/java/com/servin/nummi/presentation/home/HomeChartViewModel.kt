package com.servin.nummi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeChartViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _chartState = MutableStateFlow<List<Transaction>>(emptyList())
    val chartState: StateFlow<List<Transaction>> = _chartState.asStateFlow()

    // Consider adding isLoading and error states to your UI
    // private val _isLoading = MutableStateFlow(false)
    // val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // private val _error = MutableStateFlow<String?>(null)
    // val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            // _isLoading.value = true // Set loading state
            // _error.value = null    // Clear previous errors
            getTransactionsUseCase().collect { result -> // result is Result<List<Transaction>>
                result.fold(
                    onSuccess = { transactionList -> // transactionList is List<Transaction>
                        _chartState.value = filterAndSortTransactions(transactionList)
                        // _isLoading.value = false
                    },
                    onFailure = { exception ->
                        // Handle error, e.g., update an error StateFlow
                        // _error.value = exception.message ?: "Unknown error"
                        // _isLoading.value = false
                        // Optionally, set chartState to empty or a previous state
                        _chartState.value = emptyList()
                    }
                )
            }
        }
    }

    private fun filterAndSortTransactions(transactions: List<Transaction>): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        val tenDaysAgo = calendar.time

        return transactions
            .filter { it.date >= tenDaysAgo }
            .sortedByDescending { it.date }
    }
}