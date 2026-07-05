package com.example.edubank.domain.repository

import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.Student
import com.example.edubank.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface StudentRepository {

    fun getStudentData(studentId: String): Flow<Resource<Student>>

    fun getStudentTransactions(studentId: String): Flow<Resource<List<Transaction>>>

    suspend fun pairStudentWithParent(parentId: String, qrPairingCode: String): Resource<Unit>
}