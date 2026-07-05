package com.example.edubank.presentation.auth.student_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentLoginState())
    val state: StateFlow<StudentLoginState> = _state.asStateFlow()

    fun onClassCodeChanged(code: String) {
        _state.update { it.copy(classCode = code.uppercase(), errorMessage = null) }
    }

    fun onUsernameChanged(name: String) {
        _state.update { it.copy(username = name, errorMessage = null) }
    }

    fun onPinNumberPressed(number: Int) {
        val currentPin = _state.value.pin
        if (currentPin.length < 4) {
            _state.update { it.copy(pin = currentPin + number, errorMessage = null) }

            if (_state.value.pin.length == 4) {
                loginStudent()
            }
        }
    }

    fun onPinBackspace() {
        val currentPin = _state.value.pin
        if (currentPin.isNotEmpty()) {
            _state.update { it.copy(pin = currentPin.dropLast(1)) }
        }
    }

    private fun loginStudent() {
        val currentState = _state.value

        if (currentState.classCode.isBlank() || currentState.username.isBlank()) {
            _state.update { it.copy(errorMessage = "¡Falta tu clase o tu nombre de héroe!", pin = "") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.loginStudent(
                classCode = currentState.classCode,
                username = currentState.username,
                pin = currentState.pin
            )

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "¡Ups! PIN incorrecto",
                        pin = ""
                    ) }
                }
                is Resource.Loading -> { }
            }
        }
    }
}