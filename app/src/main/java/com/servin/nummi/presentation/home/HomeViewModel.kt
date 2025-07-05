// Ruta: com.servin.nummi.presentation.home/HomeViewModel.kt
package com.servin.nummi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.model.Budget
import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.usecase.budget.GetBudgetUseCase
import com.servin.nummi.domain.usecase.budget.SaveBudgetUseCase
import com.servin.nummi.domain.usecase.salary.GetSalaryUseCase
import com.servin.nummi.domain.usecase.salary.SaveSalaryUseCase
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getSalaryUseCase: GetSalaryUseCase,
    private val saveSalaryUseCase: SaveSalaryUseCase,
    private val getBudgetUseCase: GetBudgetUseCase,
    private val saveBudgetUseCase: SaveBudgetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadTransactions()
            checkSalaryStatus()
            checkBudgetStatus()
        }
    }

    private suspend fun loadTransactions() {
        getTransactionsUseCase()
            .onEach { result ->
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { transactions ->
                            currentState.copy(
                                isLoading = false,
                                transactions = transactions,
                                error = null
                            )
                        },
                        onFailure = { throwable ->
                            currentState.copy(
                                isLoading = false,
                                error = throwable.localizedMessage ?: "Ocurrió un error al cargar transacciones"
                            )
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkSalaryStatus() {
        viewModelScope.launch {
            getSalaryUseCase().onEach { result ->
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { salary ->
                            if (salary == null) {
                                currentState.copy(
                                    isSalaryRegistered = false,
                                    showSalaryInputDialog = true,
                                    currentSalary = null // Ensure salary is null if not registered
                                )
                            } else {
                                currentState.copy(
                                    isSalaryRegistered = true,
                                    showSalaryInputDialog = false,
                                    currentSalary = salary // Store the fetched salary
                                )
                            }
                        },
                        onFailure = { throwable ->
                            currentState.copy(
                                error = throwable.localizedMessage ?: "Error al verificar salario",
                                showSalaryInputDialog = false,
                                currentSalary = null // Ensure salary is null on error
                            )
                        }
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun checkBudgetStatus() {
        viewModelScope.launch {
            getBudgetUseCase().onEach { result ->
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { budget ->
                            if (budget == null) {
                                currentState.copy(
                                    isBudgetRegistered = false,
                                    showBudgetInputDialog = true,
                                    currentBudget = null
                                )
                            } else {
                                currentState.copy(
                                    isBudgetRegistered = true,
                                    showBudgetInputDialog = false,
                                    currentBudget = budget
                                )
                            }
                        },
                        onFailure = { throwable ->
                            currentState.copy(
                                error = throwable.localizedMessage ?: "Error al verificar presupuesto",
                                showBudgetInputDialog = false,
                                currentBudget = null
                            )
                        }
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    fun saveSalary(amount: Double) {
        viewModelScope.launch {
            val newSalary = Salary(monthlySalary = amount, startDate = Date())
            saveSalaryUseCase(newSalary).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSalaryRegistered = true,
                            showSalaryInputDialog = false,
                            currentSalary = newSalary,
                            error = null
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(error = throwable.localizedMessage ?: "Error al guardar el salario")
                    }
                }
            )
        }
    }

    fun saveBudget(amount: Double) {
        viewModelScope.launch {
            val newBudget = Budget(currentBudget = amount, lastUpdated = Date())
            saveBudgetUseCase(newBudget).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isBudgetRegistered = true,
                            showBudgetInputDialog = false,
                            currentBudget = newBudget,
                            error = null
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(error = throwable.localizedMessage ?: "Error al guardar el presupuesto")
                    }
                }
            )
        }
    }

    fun onSalaryInputDialogDismiss() {
        _uiState.update { it.copy(showSalaryInputDialog = false) }
    }

    fun onBudgetInputDialogDismiss() {
        _uiState.update { it.copy(showBudgetInputDialog = false) }
    }
}
