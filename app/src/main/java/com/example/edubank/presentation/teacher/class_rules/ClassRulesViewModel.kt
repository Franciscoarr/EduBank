package com.example.edubank.presentation.teacher.class_rules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.CustomReward
import com.example.edubank.domain.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassRulesViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ClassRulesState())
    val state: StateFlow<ClassRulesState> = _state.asStateFlow()

    val classId: String = savedStateHandle.get<String>("classId") ?: ""

    init {
        if (classId.isNotEmpty()) {
            loadRules()
        }
    }

    private fun loadRules() {
        viewModelScope.launch {
            teacherRepository.getCustomRewardsByClass(classId).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, rules = result.data) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun saveRule(reward: CustomReward) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (reward.id.isEmpty()) {
                teacherRepository.createCustomReward(reward)
            } else {
                teacherRepository.updateCustomReward(reward)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun deleteRule(rewardId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            teacherRepository.deleteCustomReward(rewardId)
            _state.update { it.copy(isLoading = false) }
        }
    }
}