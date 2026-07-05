package com.example.edubank.presentation.auth.student_login

data class StudentLoginState(
    val classCode: String = "",
    val username: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)