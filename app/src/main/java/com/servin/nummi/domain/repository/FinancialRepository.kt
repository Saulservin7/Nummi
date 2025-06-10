package com.servin.nummi.domain.repository

import com.servin.nummi.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface FinancialRepository {

    /**
     * Obtiene un flujo de transacciones en tiempo real.
     * 1. Ya no es 'suspend' porque la función en sí no bloquea, devuelve un Flow inmediatamente.
     * La suspensión ocurre cuando el colector (.collect()) empieza a escuchar el flujo.
     * 2. Devuelve un Flow que emite un Result. Esto nos permite enviar la lista de transacciones
     * en caso de éxito, o un error (excepción) en caso de fallo, sin romper el flujo.
     */
    suspend fun getTransactions(): Flow<Result<List<Transaction>>>

    /**
     * Agrega una nueva transacción.
     * 3. Devuelve un Result<Unit> para notificar si la operación fue exitosa (Unit) o fallida.
     * Esto es mucho más seguro que no devolver nada.
     */
    suspend fun addTransaction(transaction: Transaction): Result<Unit>
}