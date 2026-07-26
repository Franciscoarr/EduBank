package com.example.edubank.presentation.teacher.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubank.core.utils.Resource
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
class TeacherDashboardViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TeacherDashboardState())
    val state: StateFlow<TeacherDashboardState> = _state.asStateFlow()

    init {
        loadTeacherClasses()
    }

    private fun loadTeacherClasses() {
        val teacherId = authRepository.currentUserUid

        if (teacherId == null) {
            _state.update { it.copy(isLoading = false, errorMessage = "Error: Sesión no encontrada") }
            return
        }

        viewModelScope.launch {
            teacherRepository.getTeacherClasses(teacherId).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, classes = result.data, errorMessage = null)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}