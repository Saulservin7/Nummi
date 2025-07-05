// File: app/src/main/java/com/servin/nummi/presentation/savinggoals/SavingGoalViewModel.kt
package com.servin.nummi.presentation.saving

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.model.Budget // Import Budget
import com.servin.nummi.domain.model.Priority
import com.servin.nummi.domain.model.Salary // Import Salary
import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.domain.usecase.budget.GetBudgetUseCase // Import GetBudgetUseCase
import com.servin.nummi.domain.usecase.salary.GetSalaryUseCase // Import GetSalaryUseCase
import com.servin.nummi.domain.usecase.savings.AddSavingGoalUseCase
import com.servin.nummi.domain.usecase.savings.GetSavingGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn // Import launchIn
import kotlinx.coroutines.flow.onEach // Import onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddSavingGoalState {
    object Idle : AddSavingGoalState
    object Loading : AddSavingGoalState
    data class Success(val message: String) : AddSavingGoalState
    data class Error(val message: String) : AddSavingGoalState
}

sealed interface LoadSavingGoalsState {
    object Loading : LoadSavingGoalsState
    object Success : LoadSavingGoalsState
    data class Error(val message: String) : LoadSavingGoalsState
}

@HiltViewModel
class SavingGoalViewModel @Inject constructor(
    private val addSavingGoalUseCase: AddSavingGoalUseCase,
    private val getSavingGoalUseCase: GetSavingGoalUseCase,
    private val getSalaryUseCase: GetSalaryUseCase, // Injected GetSalaryUseCase
    private val getBudgetUseCase: GetBudgetUseCase // Injected GetBudgetUseCase
) : ViewModel() {

    private val _savingGoals = MutableStateFlow<List<SavingGoal>>(emptyList())
    val savingGoals: StateFlow<List<SavingGoal>> = _savingGoals.asStateFlow()

    private val _addGoalState = MutableStateFlow<AddSavingGoalState>(AddSavingGoalState.Idle)
    val addGoalState: StateFlow<AddSavingGoalState> = _addGoalState.asStateFlow()

    private val _loadGoalsState = MutableStateFlow<LoadSavingGoalsState>(LoadSavingGoalsState.Loading)
    val loadGoalsState: StateFlow<LoadSavingGoalsState> = _loadGoalsState.asStateFlow()

    private val _currentBudget = MutableStateFlow<Budget?>(null)
    val currentBudget: StateFlow<Budget?> = _currentBudget.asStateFlow()

    private val _biWeeklySalary = MutableStateFlow<Double?>(null)
    val biWeeklySalary: StateFlow<Double?> = _biWeeklySalary.asStateFlow()

    private val _totalSavingGoalsAmount = MutableStateFlow<Double>(0.0)
    val totalSavingGoalsAmount: StateFlow<Double> = _totalSavingGoalsAmount.asStateFlow()

    init {
        loadSavingGoals()
        loadCurrentBudget()
        loadBiWeeklySalary()
    }

    private fun loadSavingGoals() {
        viewModelScope.launch {
            _loadGoalsState.value = LoadSavingGoalsState.Loading
            getSavingGoalUseCase().collect { result ->
                result.fold(
                    onSuccess = { goals ->
                        _savingGoals.value = goals
                        _totalSavingGoalsAmount.value = goals.sumOf { it.targetAmount } // Calculate total
                        _loadGoalsState.value = LoadSavingGoalsState.Success
                    },
                    onFailure = { exception ->
                        _loadGoalsState.value = LoadSavingGoalsState.Error(
                            exception.message ?: "Unknown error loading goals"
                        )
                    }
                )
            }
        }
    }

    private fun loadCurrentBudget() {
        viewModelScope.launch { // Corrected: Wrap in viewModelScope.launch
            getBudgetUseCase().onEach { result ->
                result.fold(
                    onSuccess = { budget -> _currentBudget.value = budget },
                    onFailure = { _currentBudget.value = null /* Handle error appropriately */ }
                )
            }.launchIn(viewModelScope)
        }
    }

    private fun loadBiWeeklySalary() {
        viewModelScope.launch { // Corrected: Wrap in viewModelScope.launch
            getSalaryUseCase().onEach { result ->
                result.fold(
                    onSuccess = { salary ->
                        _biWeeklySalary.value = salary?.monthlySalary?.div(2)
                    },
                    onFailure = { _biWeeklySalary.value = null /* Handle error appropriately */ }
                )
            }.launchIn(viewModelScope)
        }
    }

    fun addSavingGoal(
        name: String,
        targetAmountStr: String,
        currentAmountStr: String,
        priority: Priority
    ) {
        viewModelScope.launch {
            _addGoalState.value = AddSavingGoalState.Loading
            val targetAmount = targetAmountStr.toDoubleOrNull()
            val currentAmount = currentAmountStr.toDoubleOrNull() ?: 0.0

            if (name.isBlank()) {
                _addGoalState.value = AddSavingGoalState.Error("Goal name cannot be empty.")
                return@launch
            }
            if (targetAmount == null || targetAmount <= 0) {
                _addGoalState.value =
                    AddSavingGoalState.Error("Target amount must be a positive number.")
                return@launch
            }
            if (currentAmount < 0) {
                _addGoalState.value = AddSavingGoalState.Error("Current amount cannot be negative.")
                return@launch
            }
            if (currentAmount > targetAmount) {
                _addGoalState.value =
                    AddSavingGoalState.Error("Current amount cannot be greater than target amount.")
                return@launch
            }

            val newGoal = SavingGoal(
                name = name,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                priority = priority
                // id and date will be handled by repository/data class default
            )

            val result = addSavingGoalUseCase(newGoal)
            result.fold(
                onSuccess = {
                    _addGoalState.value =
                        AddSavingGoalState.Success("Saving goal added successfully!")
                    // Optionally, you might want to reload or update the goals list here
                    // to reflect the new total immediately after adding a goal.
                    // For now, it will update when loadSavingGoals is next called (e.g. on screen init)
                },
                onFailure = { exception ->
                    _addGoalState.value =
                        AddSavingGoalState.Error(exception.message ?: "Failed to add saving goal")
                }
            )
        }
    }

    fun resetAddGoalState() {
        _addGoalState.value = AddSavingGoalState.Idle
    }
}
