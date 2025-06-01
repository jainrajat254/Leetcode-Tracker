package com.example.leetcode.data.repository

import com.example.leetcode.domain.model.Contest
import com.example.leetcode.domain.model.Socials
import com.example.leetcode.domain.repository.UserRepository
import com.example.leetcode.domain.service.ApiService

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {

    override suspend fun questionsSolved(username: String): List<String> {
        return apiService.questionsSolved(username)
    }

    override suspend fun lastThirtyDays(username: String): List<Boolean> {
        return apiService.lastThirtyDays(username)
    }

    override suspend fun nameAndLanguage(username: String): List<String> {
        return apiService.nameAndLanguage(username)
    }

    override suspend fun getUserSocials(username: String): Socials {
        return apiService.getUserSocials(username)
    }

    override suspend fun getContestInfo(username: String): Contest {
        return apiService.getContestInfo(username)
    }

    override suspend fun getUserProfile(username: String): Socials {
        return apiService.getUserProfile(username)
    }
}