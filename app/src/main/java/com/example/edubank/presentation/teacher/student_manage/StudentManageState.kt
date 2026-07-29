package com.example.edubank.presentation.teacher.student_manage

import com.example.edubank.domain.model.CustomReward
import com.example.edubank.domain.model.Student

data class StudentManageState(
    val isLoading: Boolean = true,
    val student: Student? = null,
    val manualRewards: List<CustomReward> = emptyList(),
    val errorMessage: String? = null,
    val isTransactionLoading: Boolean = false
)