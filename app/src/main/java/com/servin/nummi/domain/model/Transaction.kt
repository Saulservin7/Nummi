package com.servin.nummi.domain.model

import java.util.Date

// El enum no cambia.
enum class TransactionType (val type: String) {
    EXPENSE("Gasto"),
    INCOME("Ingreso")
}

enum class CategoryType(val type: String) {
    FOOD("Comida"),
    TRANSPORT("Transporte"),
    ENTERTAINMENT("Entretenimiento"),
    HEALTH("Salud"),
    UTILITIES("Servicios Públicos"),
    OTHER("Otro")
}

/**
 * Representa una única transacción financiera.
 * CORRECCIÓN: Ahora todos los parámetros del constructor primario tienen un valor por defecto.
 * Esto permite que Kotlin genere un constructor sin argumentos de forma automática para Firestore.
 */
data class Transaction(
    val id: String = "",                          // No cambia, ya tenía valor por defecto.
    val userId: String = "",                      // CORRECCIÓN: Añadido valor por defecto.
    val amount: Double = 0.0,                     // CORRECCIÓN: Añadido valor por defecto.
    val type: TransactionType = TransactionType.EXPENSE, // CORRECCIÓN: Añadido valor por defecto.
    val category: CategoryType=CategoryType.OTHER,                    // CORRECCIÓN: Añadido valor por defecto.
    val description: String? = null,              // No cambia, ya era nullable con valor por defecto.
    val date: Date = Date()                       // CORRECCIÓN: Añadido valor por defecto.
)
// CORRECCIÓN: El constructor secundario se elimina por completo. Ya no es necesario.