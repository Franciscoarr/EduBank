package com.example.edubank.presentation.teacher.student_manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.Transaction
import com.example.edubank.domain.repository.AuthRepository
import com.example.edubank.domain.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentManageViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(StudentManageState())
    val state: StateFlow<StudentManageState> = _state.asStateFlow()

    private val studentId: String = savedStateHandle.get<String>("studentId") ?: ""

    init {
        if (studentId.isNotEmpty()) {
            loadStudent(studentId)
        }
    }

    private fun loadStudent(id: String) {
        viewModelScope.launch {
            teacherRepository.getStudentById(id).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, student = result.data, errorMessage = null)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun processTransaction(amount: Double, reason: String, isIncome: Boolean) {
        val student = _state.value.student ?: return
        val teacherId = authRepository.currentUserUid ?: "profesor_de_prueba_123"

        val transaction = Transaction(
            studentId = student.id,
            teacherId = teacherId,
            amount = amount,
            reason = reason,
            isIncome = isIncome
        )

        viewModelScope.launch {
            _state.update { it.copy(isTransactionLoading = true) }

            val result = teacherRepository.processTransaction(transaction)

            _state.update { it.copy(isTransactionLoading = false) }

            if (result is Resource.Error) {
                _state.update { it.copy(errorMessage = result.message) }
            }
        }
    }
}