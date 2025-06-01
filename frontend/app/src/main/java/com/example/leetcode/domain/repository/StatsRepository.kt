package com.example.leetcode.domain.repository

import com.example.leetcode.domain.model.Stats

interface StatsRepository {

    suspend fun questionsCount(lang: String): List<Stats>
}