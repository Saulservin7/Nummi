package com.servin.nummi.domain.model

data class PurchaseGoal(
    val id: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val priority: Priority = Priority.MEDIUM
)
