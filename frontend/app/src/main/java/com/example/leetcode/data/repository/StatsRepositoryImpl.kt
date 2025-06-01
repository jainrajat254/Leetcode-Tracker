package com.example.leetcode.data.repository

import com.example.leetcode.domain.model.Stats
import com.example.leetcode.domain.repository.StatsRepository
import com.example.leetcode.domain.service.ApiService

class StatsRepositoryImpl(private val apiService: ApiService): StatsRepository {

    override suspend fun questionsCount(lang: String): List<Stats> {
        return apiService.questionsCount(lang)
    }
}