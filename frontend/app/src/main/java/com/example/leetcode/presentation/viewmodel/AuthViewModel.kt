package com.example.leetcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leetcode.domain.model.LoginCredentials
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.model.UserData
import com.example.leetcode.domain.repository.AuthRepository
import com.example.leetcode.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<ResultState<UserData>>(ResultState.Idle)
    val registerState: StateFlow<ResultState<UserData>> get() = _registerState

    private val _loginState = MutableStateFlow<ResultState<LoginResponse>>(ResultState.Idle)
    val loginState: StateFlow<ResultState<LoginResponse>> get() = _loginState

    fun registerUser(user: UserData) {
        _registerState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val registerResponse: UserData = authRepository.registerUser(user)
                _registerState.value = ResultState.Success(registerResponse)
            } catch (e: Exception) {
                _registerState.value = ResultState.Error(e)
            }
        }
    }

    fun loginUser(user: LoginCredentials) {
        _loginState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val loginResponse: LoginResponse = authRepository.loginUser(user)
                _loginState.value = ResultState.Success(loginResponse)
            } catch (e: Exception) {
                _loginState.value = ResultState.Error(e)
            }
        }
    }

    fun isValidUser(username: String, onResult: (Map<String, String>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.isValidUser(username)
                onResult(response)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                onResult(mapOf("status" to "false", "error" to (errorBody ?: "Invalid username")))
            } catch (e: Exception) {
                onResult(mapOf("status" to "false", "error" to "Something went wrong"))
            }
        }
    }

    fun clear() {
        _loginState.value = ResultState.Idle
        _registerState.value = ResultState.Idle
    }

}