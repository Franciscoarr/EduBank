package com.example.edubank.presentation.teacher.class_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ClassDetailState())
    val state: StateFlow<ClassDetailState> = _state.asStateFlow()

    init {
        val classId: String = savedStateHandle.get<String>("classId") ?: ""
        if (classId.isNotEmpty()) {
            loadStudents(classId)
        } else {
            _state.update { it.copy(isLoading = false, errorMessage = "Error: Clase no encontrada") }
        }
    }

    private fun loadStudents(classId: String) {
        viewModelScope.launch {
            teacherRepository.getStudentsByClass(classId).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, students = result.data, errorMessage = null)
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}