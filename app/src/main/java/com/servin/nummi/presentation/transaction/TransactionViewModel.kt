package com.servin.nummi.presentation.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.model.AddTransactionScreenState
import com.servin.nummi.domain.model.CategoryType
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.model.TransactionType
import com.servin.nummi.domain.usecase.transactions.AddTransactionUseCase
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionScreenState())
    val uiState = _uiState.asStateFlow()

    fun onTypeChange(newType: TransactionType) {
        _uiState.update { it.copy(type = newType) }
    }

    fun onAmountChange(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onDropdownExpandedChange(isExpanded: Boolean) {
        _uiState.update { it.copy(isExpanded = isExpanded) }
    }

    fun onCategoryChange(newCategory: CategoryType) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun addTransactionFromState() {
        viewModelScope.launch {
            // Set loading state and reset previous transaction/error states
            _uiState.update { it.copy(isLoading = true, error = null, transactionAdded = false) }

            val currentState = _uiState.value // Get current state after setting isLoading to true

            // Validate and convert the amount
            val amountDouble = currentState.amount.toDoubleOrNull()
            if (amountDouble == null || amountDouble <= 0) {
                Log.e("TransactionViewModel", "Cantidad inválida: ${currentState.amount}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Cantidad inválida. Debe ser un número mayor que cero."
                    )
                }
                return@launch // Exit this coroutine block
            }

            // TODO: Implementar la obtención del userId. Ejemplo:
            // val userId = authRepository.currentUser?.uid
            // if (userId == null) {
            //     _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado.") }
            //     return@launch // Exit this coroutine block
            // }
            // Using a placeholder until actual userId logic is implemented:
            val userId = "placeholder_user_id" // Replace with actual user ID retrieval

            // Construct the Transaction object
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                userId = userId, // Use the actual userId
                type = currentState.type,
                amount = amountDouble, // Use the validated and converted Double
                category = currentState.category,
                description = currentState.description.takeIf { it.isNotBlank() },
                date = Date()
            )

            // Call the use case
            val result = addTransactionUseCase(transaction)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            transactionAdded = true,
                            isLoading = false,
                            error = null,
                            // Optionally reset form fields
                            amount = "",
                            description = "",
                            // type = TransactionType.EXPENSE, // Reset to default if needed
                            // category = CategoryType.OTHER // Reset to default if needed
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            error = exception.message ?: "Error al agregar la transacción",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun resetTransactionAddedState() {
        _uiState.update { it.copy(transactionAdded = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}