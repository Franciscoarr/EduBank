package com.example.edubank.domain.repository

import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.Classroom
import com.example.edubank.domain.model.Student
import com.example.edubank.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    fun getTeacherClasses(teacherId: String): Flow<Resource<List<Classroom>>>

    fun getStudentsByClass(classId: String): Flow<Resource<List<Student>>>

    suspend fun processTransaction(transaction: Transaction): Resource<Unit>

    suspend fun createClass(name: String, grade: String, teacherId: String): Resource<Unit>

    suspend fun createStudent(classId: String, teacherId: String, username: String, pin: String): Resource<Unit>

    fun getStudentById(studentId: String): Flow<Resource<Student>>
}