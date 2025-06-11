package com.servin.nummi.presentation.transactionlist

import androidx.compose.ui.res.integerResource
import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.model.TransactionListScreenState
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) :ViewModel(){

    private val _uiState = MutableStateFlow(TransactionListScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTransactions()
        }
    }



    private suspend fun getTransactions() {

        _uiState.update { it.copy(isLoading = true, error = null) }


        getTransactionsUseCase().collect { result: Result<List<Transaction>> ->
            result.fold(
                onSuccess = { transactions -> // transactions es List<Transaction>
                    _uiState.update {
                        it.copy(
                            // Asumiendo que TransactionListScreenState tiene 'transaction: List<Transaction>'
                            // según tu código original. Si el campo se llama 'transactions', ajústalo.
                            transaction = transactions,
                            isLoading = false,
                            error = null // Es buena práctica limpiar el error en caso de éxito
                        )
                    }
                },
                onFailure = { error -> // error es Throwable
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }





    }
}