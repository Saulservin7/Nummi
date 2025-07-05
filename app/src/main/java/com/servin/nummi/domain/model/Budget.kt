package com.servin.nummi.domain.model

import java.util.Date

/**
 * Representa el presupuesto actual del usuario.
 */
data class Budget(
    val id: String = "",
    val userId: String = "",
    val currentBudget: Double = 0.0,
    val currency: String = "MXN",
    val lastUpdated: Date = Date(),
    val isActive: Boolean = true
)
