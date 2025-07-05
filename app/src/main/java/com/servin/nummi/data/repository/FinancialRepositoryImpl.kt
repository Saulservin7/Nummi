// Ruta: com.servin.nummi.data.repository/FinancialRepositoryImpl.kt
package com.servin.nummi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration // Import ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.servin.nummi.domain.model.Budget // Import Budget
import com.servin.nummi.domain.model.Priority // Import Priority if not already imported
import com.servin.nummi.domain.model.PurchaseGoal
import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map // Import map operator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FinancialRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FinancialRepository {

    private val userId: String?
        get() = auth.currentUser?.uid

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .document()
            val transactionWithId = transaction.copy(id = documentRef.id)
            documentRef.set(transactionWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(): Flow<Result<List<Transaction>>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            val currentUserId = userId ?: run {
                trySend(Result.failure(IllegalStateException("Usuario no autenticado.")))
                close(IllegalStateException("Usuario no autenticado.")) // Close the channel
                return@callbackFlow // Exit lambda; awaitClose will still be called
            }

            listener = firestore.collection("users")
                .document(currentUserId)
                .collection("transactions").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        try {
                            val transactions = snapshot.toObjects<Transaction>()
                            trySend(Result.success(transactions))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
            close(e) // Close the channel on critical setup error
        }

        awaitClose {
            listener?.remove() // Remove listener only if it was initialized
        }
    }

    override suspend fun addSavingGoal(savingGoal: SavingGoal): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("savingGoals")
                .document()
            // Ensure the priority is saved correctly, it will be stored as a String by Firestore
            val savingGoalWithId = savingGoal.copy(id = documentRef.id)
            documentRef.set(savingGoalWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSavingGoals(): Flow<Result<List<SavingGoal>>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            val currentUserId = userId ?: run {
                trySend(Result.failure(IllegalStateException("Usuario no autenticado.")))
                close(IllegalStateException("Usuario no autenticado.")) // Close the channel
                return@callbackFlow // Exit lambda; awaitClose will still be called
            }

            // Query ordered by priority string (e.g., "HIGH", "MEDIUM", "LOW")
            // Firestore stores enums as strings.
            // Query.Direction.ASCENDING will sort them alphabetically: HIGH, LOW, MEDIUM
            // This is not HIGH, MEDIUM, LOW. Client-side sorting will be needed for exact order.
            listener = firestore.collection("users")
                .document(currentUserId)
                .collection("savingGoals")
                // .orderBy("priority", Query.Direction.ASCENDING) // This would sort HIGH, LOW, MEDIUM
                // For now, let's fetch without specific Firestore sort on priority and sort client-side
                 .orderBy("creationDate", Query.Direction.DESCENDING) // Or any other logical default sort
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        try {
                            val savingGoals = snapshot.toObjects<SavingGoal>()
                            // Client-side sorting for HML priority
                            val sortedGoals = savingGoals.sortedWith(
                                compareBy { goal ->
                                    when (goal.priority) {
                                        Priority.HIGH -> 0
                                        Priority.MEDIUM -> 1
                                        Priority.LOW -> 2
                                    }
                                }
                            )
                            trySend(Result.success(sortedGoals))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
            close(e) // Close the channel on critical setup error
        }

        awaitClose {
            listener?.remove() // Remove listener only if it was initialized
        }
    }

    override suspend fun saveSalary(salary: Salary): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("salaries")
                .document("current") // Fixed ID for the single salary document
            val salaryToSave = salary.copy(id = documentRef.id) // Ensure the salary saved has the "current" ID
            documentRef.set(salaryToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentSalary(): Flow<Result<Salary?>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            val currentUserId = userId ?: run {
                trySend(Result.failure(IllegalStateException("Usuario no autenticado.")))
                close(IllegalStateException("Usuario no autenticado."))
                return@callbackFlow
            }

            listener = firestore.collection("users")
                .document(currentUserId)
                .collection("salaries")
                .document("current") // Assuming a single salary document with fixed ID "current"
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val salary = snapshot.toObject(Salary::class.java)
                            trySend(Result.success(salary))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    } else {
                        // Document does not exist or snapshot is null
                        trySend(Result.success(null))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
            close(e)
        }

        awaitClose {
            listener?.remove()
        }
    }

    override suspend fun updateSalary(salary: Salary): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("salaries")
                .document("current") // Assuming we update the single salary document
            // Ensure the salary object saved has the correct ID, consistent with "current" document ID
            val salaryToUpdate = salary.copy(id = documentRef.id)
            documentRef.set(salaryToUpdate).await() // .set() will overwrite the document or create it if it doesn't exist.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSalary(salaryId: String): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            // Ensure salaryId is "current" if you are following the single salary document convention
            firestore.collection("users")
                .document(currentUserId)
                .collection("salaries")
                .document(salaryId) // Use the provided salaryId, which should be "current"
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveBudget(budget: Budget): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("budgets")
                .document("current") // Fixed ID for the single budget document
            val budgetToSave = budget.copy(id = documentRef.id) // Ensure the budget saved has the "current" ID
            documentRef.set(budgetToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentBudget(): Flow<Result<Budget?>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            val currentUserId = userId ?: run {
                trySend(Result.failure(IllegalStateException("Usuario no autenticado.")))
                close(IllegalStateException("Usuario no autenticado."))
                return@callbackFlow
            }

            listener = firestore.collection("users")
                .document(currentUserId)
                .collection("budgets")
                .document("current") // Assuming a single budget document with fixed ID "current"
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val budget = snapshot.toObject(Budget::class.java)
                            trySend(Result.success(budget))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    } else {
                        // Document does not exist or snapshot is null
                        trySend(Result.success(null))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
            close(e)
        }

        awaitClose {
            listener?.remove()
        }
    }

    override suspend fun updateBudget(budget: Budget): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("budgets")
                .document("current") // Assuming we update the single budget document
            // Ensure the budget object saved has the correct ID, consistent with "current" document ID
            val budgetToUpdate = budget.copy(id = documentRef.id)
            documentRef.set(budgetToUpdate).await() // .set() will overwrite the document or create it if it doesn't exist.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            // Ensure budgetId is "current" if you are following the single budget document convention
            firestore.collection("users")
                .document(currentUserId)
                .collection("budgets")
                .document(budgetId) // Use the provided budgetId, which should be "current"
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPurchaseGoal(goal: PurchaseGoal): Result<Unit> {
        return try {
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")
            val documentRef = firestore.collection("users")
                .document(currentUserId)
                .collection("purchaseGoals")
                .document()
            val purchaseGoalWithId = goal.copy(id = documentRef.id)
            documentRef.set(purchaseGoalWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPurchaseGoals(): Flow<Result<List<PurchaseGoal>>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            val currentUserId = userId ?: run {
                trySend(Result.failure(IllegalStateException("Usuario no autenticado.")))
                close(IllegalStateException("Usuario no autenticado."))
                return@callbackFlow
            }

            listener = firestore.collection("users")
                .document(currentUserId)
                .collection("purchaseGoals")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        try {
                            val purchaseGoals = snapshot.toObjects<PurchaseGoal>()
                            trySend(Result.success(purchaseGoals))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
            close(e)
        }

        awaitClose {
            listener?.remove()
        }
    }
}
