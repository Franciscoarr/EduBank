package com.example.edubank.presentation.teacher.dashboard

import com.example.edubank.domain.model.Classroom

data class TeacherDashboardState(
    val isLoading: Boolean = true,
    val classes: List<Classroom> = emptyList(),
    val errorMessage: String? = null
)