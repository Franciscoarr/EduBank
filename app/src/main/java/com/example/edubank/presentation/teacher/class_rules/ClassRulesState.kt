package com.example.edubank.presentation.teacher.class_rules

import com.example.edubank.domain.model.CustomReward

data class ClassRulesState(
    val isLoading: Boolean = true,
    val rules: List<CustomReward> = emptyList(),
    val errorMessage: String? = null
)