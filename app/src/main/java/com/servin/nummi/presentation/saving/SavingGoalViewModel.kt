// File: app/src/main/java/com/servin/nummi/presentation/savinggoals/SavingGoalViewModel.kt
package com.servin.nummi.presentation.saving

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.domain.usecase.savings.AddSavingGoalUseCase
import com.servin.nummi.domain.usecase.savings.GetSavingGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val getSavingGoalUseCase: GetSavingGoalUseCase
) : ViewModel() {

    private val _savingGoals = MutableStateFlow<List<SavingGoal>>(emptyList())
    val savingGoals: StateFlow<List<SavingGoal>> = _savingGoals.asStateFlow()

    private val _addGoalState = MutableStateFlow<AddSavingGoalState>(AddSavingGoalState.Idle)
    val addGoalState: StateFlow<AddSavingGoalState> = _addGoalState.asStateFlow()

    private val _loadGoalsState = MutableStateFlow<LoadSavingGoalsState>(LoadSavingGoalsState.Loading)
    val loadGoalsState: StateFlow<LoadSavingGoalsState> = _loadGoalsState.asStateFlow()

    init {
        loadSavingGoals()
    }

    private fun loadSavingGoals() {
        viewModelScope.launch {
            _loadGoalsState.value = LoadSavingGoalsState.Loading
            getSavingGoalUseCase().collect { result ->
                result.fold(
                    onSuccess = { goals ->
                        _savingGoals.value = goals
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

    fun addSavingGoal(name: String, targetAmountStr: String, currentAmountStr: String) {
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
                currentAmount = currentAmount
                // id and date will be handled by repository/data class default
            )

            val result = addSavingGoalUseCase(newGoal)
            result.fold(
                onSuccess = {
                    _addGoalState.value =
                        AddSavingGoalState.Success("Saving goal added successfully!")
                    // The list will update automatically due to Firestore's real-time listener
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