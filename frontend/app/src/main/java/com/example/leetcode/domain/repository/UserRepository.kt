package com.example.leetcode.domain.repository

import com.example.leetcode.domain.model.Contest
import com.example.leetcode.domain.model.Socials

interface UserRepository {

    suspend fun questionsSolved(username: String): List<String>
    suspend fun lastThirtyDays(username: String): List<Boolean>
    suspend fun nameAndLanguage(username: String): List<String>
    suspend fun getUserSocials(username: String): Socials
    suspend fun getContestInfo(username: String): Contest
    suspend fun getUserProfile(username: String): Socials

}