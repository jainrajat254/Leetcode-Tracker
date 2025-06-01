package com.example.leetcode.data.repository

import com.example.leetcode.domain.model.StreakContent
import com.example.leetcode.domain.repository.HomeRepository
import com.example.leetcode.domain.service.ApiService

class HomeRepositoryImpl(private val apiService: ApiService) : HomeRepository {

    override suspend fun updateAll() {
        return apiService.updateAll()
    }

    override suspend fun hasAttemptedToday(selectedLanguage: String): List<StreakContent> {
        return apiService.hasAttemptedToday(selectedLanguage)
    }
}