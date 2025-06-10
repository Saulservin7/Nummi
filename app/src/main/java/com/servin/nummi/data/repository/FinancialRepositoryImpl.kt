// Ruta: com.servin.nummi.data.repository/FinancialRepositoryImpl.kt
package com.servin.nummi.data.repository

import com.google.firebase.auth.FirebaseAuth // 1. Importamos FirebaseAuth.
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FinancialRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    // 2. Inyectamos FirebaseAuth para poder obtener el UID del usuario logueado.
    private val auth: FirebaseAuth
) : FinancialRepository {

    // Obtenemos el UID del usuario actual. Si no hay usuario, las operaciones fallarán (lo cual es correcto).
    private val userId: String?
        get() = auth.currentUser?.uid

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return try {
            // 3. Verificamos que tenemos un ID de usuario. Si no, es un error.
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")

            // Creamos un nuevo documento en la colección de transacciones.
            // Firestore generará un ID único para este documento.
            val documentRef = firestore.collection("users") // Colección principal "users"
                .document(currentUserId) // Documento específico para el usuario logueado
                .collection("transactions") // Sub-colección de sus transacciones
                .document() // Nuevo documento para la transacción

            // 4. Asignamos el ID generado por Firestore a nuestro objeto antes de guardarlo.
            // Esto es CRUCIAL.
            val transactionWithId = transaction.copy(id = documentRef.id)

            // Guardamos el objeto completo en Firestore.
            documentRef.set(transactionWithId).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(): Flow<Result<List<Transaction>>> = callbackFlow {
        try {
            // 5. Verificamos el ID de usuario también para la lectura.
            val currentUserId = userId ?: throw IllegalStateException("Usuario no autenticado.")

            // Escuchamos cambios en TIEMPO REAL en la colección de transacciones del usuario.
            val listener = firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Si hay un error de Firestore, lo enviamos al Flow.
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        // Convertimos los documentos del snapshot a nuestra lista de objetos Transaction.
                        val transactions = snapshot.toObjects<Transaction>()
                        // Enviamos la lista exitosamente a través del Flow.
                        trySend(Result.success(transactions))
                    }
                }
            // Esto se ejecuta cuando el Flow se cancela (ej. el ViewModel se destruye).
            // Es importante para evitar fugas de memoria.
            awaitClose { listener.remove() }

        } catch (e: Exception) {
            // Enviamos cualquier otra excepción que pueda ocurrir.
            trySend(Result.failure(e))
        }
    }
}