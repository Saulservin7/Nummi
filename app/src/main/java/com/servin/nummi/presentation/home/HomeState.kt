// Ruta: com.servin.nummi.presentation.home/HomeState.kt
package com.servin.nummi.presentation.home

import com.servin.nummi.domain.model.Budget
import com.servin.nummi.domain.model.Salary // Import Salary
import com.servin.nummi.domain.model.Transaction

/**
 * Representa todos los estados posibles de la HomeScreen.
 * Es una única clase que la UI observará.
 */
data class HomeState(
    // Indica si se está realizando una carga inicial de datos.
    val isLoading: Boolean = true,
    // Contiene la lista de transacciones si la carga fue exitosa.
    val transactions: List<Transaction> = emptyList(),
    // Contiene un mensaje de error si algo falló.
    val error: String? = null,
    // Indica si el salario mensual del usuario ya ha sido registrado.
    val isSalaryRegistered: Boolean = false, // Nuevo campo
    // Indica si se debe mostrar el diálogo para ingresar el salario.
    val showSalaryInputDialog: Boolean = false, // Nuevo campo
    val currentSalary: Salary? = null, // New field to hold the salary
    // Indica si el presupuesto actual del usuario ya ha sido registrado.
    val isBudgetRegistered: Boolean = false,
    // Indica si se debe mostrar el diálogo para ingresar el presupuesto.
    val showBudgetInputDialog: Boolean = false,
    val currentBudget: Budget? = null
)
