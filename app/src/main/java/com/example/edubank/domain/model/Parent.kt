package com.example.edubank.domain.model

data class Parent(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val childrenIds: List<String> = emptyList()
)