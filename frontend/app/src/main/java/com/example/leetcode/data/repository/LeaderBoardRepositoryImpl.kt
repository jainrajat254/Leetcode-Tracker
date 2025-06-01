package com.example.leetcode.data.repository

import com.example.leetcode.domain.model.LeaderBoard
import com.example.leetcode.domain.repository.LeaderBoardRepository
import com.example.leetcode.domain.service.ApiService

class LeaderBoardRepositoryImpl(private val apiService: ApiService) : LeaderBoardRepository {

    override suspend fun clubLeaderBoard(): List<LeaderBoard> {
        return apiService.clubLeaderBoard()
    }

    override suspend fun languageLeaderBoard(selectedLanguage: String): List<LeaderBoard> {
        return apiService.languageLeaderBoard(selectedLanguage)
    }
}