package com.example.edubank.domain.model

data class Transaction(
    val id: String = "",
    val studentId: String = "",
    val teacherId: String = "",
    val amount: Double = 0.0,
    val reason: String = "",
    val timestamp: Long = 0L,
    val isIncome: Boolean = true
)