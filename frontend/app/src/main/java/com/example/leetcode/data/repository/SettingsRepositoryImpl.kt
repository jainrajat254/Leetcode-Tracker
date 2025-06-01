package com.example.leetcode.data.repository

import com.example.leetcode.domain.model.EditDetails
import com.example.leetcode.domain.model.EditPassword
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.repository.SettingsRepository
import com.example.leetcode.domain.service.ApiService

class SettingsRepositoryImpl(private val apiService: ApiService) : SettingsRepository {

    override suspend fun updateUser(username: String) {
        return apiService.updateUser(username = username)
    }

    override suspend fun editPassword(id: String, data: EditPassword): String {
        return apiService.editPassword(id = id, data = data)
    }

    override suspend fun editDetails(id: String, data: EditDetails): EditDetails {
        return apiService.editDetails(id = id, data = data)
    }
}