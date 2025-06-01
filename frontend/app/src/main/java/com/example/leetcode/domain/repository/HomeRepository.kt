package com.example.leetcode.domain.repository

import com.example.leetcode.domain.model.StreakContent

interface HomeRepository {

    suspend fun updateAll()
    suspend fun hasAttemptedToday(selectedLanguage: String): List<StreakContent>
}