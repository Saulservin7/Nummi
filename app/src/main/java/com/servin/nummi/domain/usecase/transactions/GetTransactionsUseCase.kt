// Ruta: com.servin.nummi.domain.usecase.transactions/GetTransactionsUseCase.kt
package com.servin.nummi.domain.usecase.transactions

import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.Result

/**
 * Caso de uso para obtener las transacciones. Su única función es llamar al repositorio.
 * Pertenece a la capa de dominio.
 */
class GetTransactionsUseCase @Inject constructor(
    private val repository: FinancialRepository // Depende de la interfaz del repositorio.
) {
    /**
     * La sobrecarga del operador 'invoke' permite llamar a la clase como una función.
     * 1. NO es 'suspend': La función en sí no es asíncrona, solo devuelve un Flow.
     * La asincronía ocurre cuando se consume el Flow en el ViewModel.
     * 2. DEVUELVE Flow<Result<...>>: Pasa directamente el tipo de dato que define el repositorio.
     * Esto asegura que los errores y los éxitos se propaguen correctamente hacia el ViewModel.
     */
    suspend operator fun invoke(): Flow<Result<List<Transaction>>> = repository.getTransactions()
}