package com.example.edubank.domain.model

data class CustomReward(
    val id: String = "",
    val classId: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val isIncome: Boolean = true,
    val autoDayOfMonth: Int? = null
)