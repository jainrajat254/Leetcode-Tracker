package com.example.leetcode.data.repository

import com.example.leetcode.domain.model.LoginCredentials
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.model.UserData
import com.example.leetcode.domain.repository.AuthRepository
import com.example.leetcode.domain.service.ApiService
import retrofit2.Response

class AuthRepositoryImpl(private val apiService: ApiService) : AuthRepository {
    override suspend fun registerUser(user: UserData): UserData {
        return apiService.registerUser(user = user)
    }

    override suspend fun loginUser(user: LoginCredentials): LoginResponse {
        return apiService.loginUser(user = user)
    }

    override suspend fun isValidUser(username: String): Map<String, String> {
        return apiService.isValidUser(username)
    }
}