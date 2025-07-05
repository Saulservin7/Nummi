package com.servin.nummi.domain.model

import java.util.Date

/**
 * Representa el salario mensual del usuario
 */
data class Salary(
    val id: String = "",
    val userId: String = "",
    val monthlySalary: Double = 0.0,
    val biweeklyAmount: Double = 0.0, // Calculado automáticamente como monthlySalary / 2
    val currency: String = "MXN",
    val startDate: Date = Date(),
    val isActive: Boolean = true
) {
    /**
     * Calcula el monto quincenal basado en el salario mensual
     */
    fun calculateBiweeklyAmount(): Double = monthlySalary / 2.0

    /**
     * Calcula el monto diario promedio
     */
    fun calculateDailyAmount(): Double = monthlySalary / 30.0
}
