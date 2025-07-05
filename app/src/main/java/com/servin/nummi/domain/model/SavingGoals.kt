package com.servin.nummi.domain.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

// Define an enum for Priority

data class SavingGoal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val creationDate: Date = Date(),
    val priority: Priority = Priority.MEDIUM // New field for priority
)