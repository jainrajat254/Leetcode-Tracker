package com.example.leetcode.domain.model

data class LoginResponse(
    val name: String,
    val id: String,
    val selectedLanguage: String,
    val year: String,
    val username: String,
    val token: String
)