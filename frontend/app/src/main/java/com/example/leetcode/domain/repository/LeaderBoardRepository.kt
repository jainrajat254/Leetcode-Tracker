package com.example.leetcode.domain.repository

import com.example.leetcode.domain.model.LeaderBoard

interface LeaderBoardRepository {

    suspend fun clubLeaderBoard(): List<LeaderBoard>

    suspend fun languageLeaderBoard(selectedLanguage: String): List<LeaderBoard>
}