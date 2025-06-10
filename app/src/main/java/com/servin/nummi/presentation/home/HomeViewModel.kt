// Ruta: com.servin.nummi.presentation.home/HomeViewModel.kt
package com.servin.nummi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadTransactions()

        }

    }

    private suspend fun loadTransactions() {
        // Ahora que getTransactionsUseCase() no es suspend y devuelve el tipo correcto, esta llamada es válida.
        getTransactionsUseCase()
            .onEach { result ->
                // La variable 'result' es ahora un objeto kotlin.Result, por lo que tiene el método 'fold'.
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { transactions ->
                            // El compilador sabe que 'transactions' es una List<Transaction>.
                            currentState.copy(
                                isLoading = false,
                                transactions = transactions,
                                error = null
                            )
                        },
                        onFailure = { throwable ->
                            // El compilador sabe que 'throwable' es un Throwable y tiene 'localizedMessage'.
                            currentState.copy(
                                isLoading = false,
                                error = throwable.localizedMessage ?: "Ocurrió un error"
                            )
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}