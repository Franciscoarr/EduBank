package com.example.edubank.presentation.student.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDashboardViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(StudentDashboardState())
    val state: StateFlow<StudentDashboardState> = _state.asStateFlow()

    init {
        val studentId: String = savedStateHandle.get<String>("studentId") ?: ""

        if (studentId.isNotEmpty()) {
            loadStudentData(studentId)
            loadRecentTransactions(studentId)
        } else {
            _state.update { it.copy(isLoading = false, errorMessage = "Error: ID de jugador no encontrado") }
        }
    }

    private fun loadStudentData(studentId: String) {
        viewModelScope.launch {
            studentRepository.getStudentData(studentId).collect { result ->
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

    private fun loadRecentTransactions(studentId: String) {
        viewModelScope.launch {
            studentRepository.getStudentTransactions(studentId).collect { result ->
                if (result is Resource.Success) {
                    _state.update { it.copy(recentTransactions = result.data.take(5)) }
                }
            }
        }
    }
}