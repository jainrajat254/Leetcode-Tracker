package com.example.leetcode.domain.repository

import com.example.leetcode.domain.model.EditDetails
import com.example.leetcode.domain.model.EditPassword
import com.example.leetcode.domain.model.LoginResponse

interface SettingsRepository {

    suspend fun updateUser(username: String)
    suspend fun editPassword(id: String, data: EditPassword): String
    suspend fun editDetails(id: String, data: EditDetails): EditDetails
}