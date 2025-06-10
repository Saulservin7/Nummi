package com.servin.nummi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FinancialRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FinancialRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    override suspend fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        // La ruta en Firestore será: users/{userId}/transactions/{transactionId}
        // Esto asegura que los datos de cada usuario están aislados y protegidos por las reglas de Firestore.
        val collection = firestore.collection("users").document(currentUserId).collection("transactions")

        // Creamos un listener que se ejecutará cada vez que haya un cambio en la colección.
        // Ordenamos por fecha para mostrar siempre lo más reciente primero.
        val listenerRegistration = collection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                // Si Firebase devuelve un error, lo propagamos al Flow para que sea manejado en capas superiores.
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                // Si el snapshot es nulo, no hay datos, enviamos una lista vacía.
                if (snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // 'toObjects' convierte automáticamente los documentos de Firestore a nuestra data class 'Transaction'.
                // Firestore usa el constructor vacío que añadimos al modelo.
                val transactions = snapshot.toObjects(Transaction::class.java)
                trySend(transactions) // Enviamos la lista actualizada a través del Flow.
            }

        // 'awaitClose' es un bloque de código que se ejecuta cuando el colector del Flow (ej. el ViewModel) se cancela.
        // Es FUNDAMENTAL para remover el listener y evitar fugas de memoria y consumo innecesario de recursos.
        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun addTransaction(transaction: Transaction) {
        try {
            firestore.collection("users").document(currentUserId).collection("transactions")
                .add(transaction).await()
        } catch (e: Exception) {
            throw e
        }
    }

}