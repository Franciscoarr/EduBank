package com.example.edubank.domain.repository

import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserUid: String?

    suspend fun getCurrentUserRole(uid: String): Resource<UserRole>

    suspend fun loginWithEmail(email: String, pass: String): Resource<String>

    suspend fun loginStudent(classCode: String, username: String, pin: String): Resource<String>

    suspend fun registerWithEmail(email: String, pass: String, role: UserRole, name: String): Resource<String>

    fun logout()
}