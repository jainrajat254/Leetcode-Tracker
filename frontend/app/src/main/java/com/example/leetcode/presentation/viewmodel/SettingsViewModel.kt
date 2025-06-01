package com.example.leetcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leetcode.domain.model.EditDetails
import com.example.leetcode.domain.model.EditPassword
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.repository.SettingsRepository
import com.example.leetcode.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _editPasswordState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val editPasswordState: StateFlow<ResultState<String>> = _editPasswordState

    private val _editDetailsState = MutableStateFlow<ResultState<EditDetails>>(ResultState.Idle)
    val editDetailsState: StateFlow<ResultState<EditDetails>> = _editDetailsState

    fun editPassword(id: String, data: EditPassword) {
        _editPasswordState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val result = settingsRepository.editPassword(id = id, data = data)
                _editPasswordState.value = ResultState.Success(result)
            } catch (e: Exception) {
                _editPasswordState.value = ResultState.Error(e)
            }
        }
    }

    fun editDetails(id: String, data: EditDetails) {
        _editDetailsState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val result = settingsRepository.editDetails(id = id, data = data)
                _editDetailsState.value = ResultState.Success(result)
                updateUser(result.username)
            } catch (e: Exception) {
                _editDetailsState.value = ResultState.Error(e)
            }
        }
    }

    fun updateUser(username: String) {
        viewModelScope.launch {
            settingsRepository.updateUser(username = username)
        }
    }

    fun resetEditDetailsState() {
        _editDetailsState.value = ResultState.Idle
    }

    fun clear() {
        _editPasswordState.value = ResultState.Idle
        _editDetailsState.value = ResultState.Idle
    }

}