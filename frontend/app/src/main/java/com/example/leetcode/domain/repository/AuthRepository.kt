package com.example.leetcode.domain.repository

import com.example.leetcode.domain.model.LoginCredentials
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.model.UserData
import retrofit2.http.Path

interface AuthRepository {

    suspend fun registerUser(user: UserData): UserData

    suspend fun loginUser(user: LoginCredentials): LoginResponse

    suspend fun isValidUser(username: String): Map<String, String>

}