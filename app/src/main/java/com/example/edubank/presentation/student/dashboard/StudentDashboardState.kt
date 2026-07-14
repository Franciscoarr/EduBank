package com.example.edubank.presentation.student.dashboard

import com.example.edubank.domain.model.Student
import com.example.edubank.domain.model.Transaction

data class StudentDashboardState(
    val isLoading: Boolean = true,
    val student: Student? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)