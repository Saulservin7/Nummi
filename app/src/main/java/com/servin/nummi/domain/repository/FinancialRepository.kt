package com.servin.nummi.domain.repository

import com.servin.nummi.domain.model.Budget
import com.servin.nummi.domain.model.PurchaseGoal
import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.model.SavingGoal
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

    suspend fun addSavingGoal(savingGoal: SavingGoal): Result<Unit>
    suspend fun getSavingGoals(): Flow<Result<List<SavingGoal>>>

    // Salary management functions
    suspend fun saveSalary(salary: Salary): Result<Unit>
    suspend fun getCurrentSalary(): Flow<Result<Salary?>>
    suspend fun updateSalary(salary: Salary): Result<Unit>
    suspend fun deleteSalary(salaryId: String): Result<Unit>

    // Budget management functions
    suspend fun saveBudget(budget: Budget): Result<Unit>
    suspend fun getCurrentBudget(): Flow<Result<Budget?>>
    suspend fun updateBudget(budget: Budget): Result<Unit>
    suspend fun deleteBudget(budgetId: String): Result<Unit>

    // PurchaseGoal management functions
    suspend fun addPurchaseGoal(goal: PurchaseGoal): Result<Unit>
    suspend fun getPurchaseGoals(): Flow<Result<List<PurchaseGoal>>>
}