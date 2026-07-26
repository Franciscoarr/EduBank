package com.example.edubank.presentation.teacher.class_detail

import com.example.edubank.domain.model.Student

data class ClassDetailState(
    val isLoading: Boolean = true,
    val students: List<Student> = emptyList(),
    val errorMessage: String? = null
)