// Ruta: com.servin.nummi.presentation.home/HomeState.kt
package com.servin.nummi.presentation.home

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
    val error: String? = null
)