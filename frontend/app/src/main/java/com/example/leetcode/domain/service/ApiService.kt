package com.example.leetcode.domain.service

import com.example.leetcode.domain.model.Contest
import com.example.leetcode.domain.model.EditDetails
import com.example.leetcode.domain.model.EditPassword
import com.example.leetcode.domain.model.LeaderBoard
import com.example.leetcode.domain.model.LoginCredentials
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.model.Socials
import com.example.leetcode.domain.model.UserData
import com.example.leetcode.domain.model.Stats
import com.example.leetcode.domain.model.StreakContent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body user: UserData): UserData

    @POST("/login")
    suspend fun loginUser(@Body user: LoginCredentials): LoginResponse

    @GET("/clubLeaderBoard")
    suspend fun clubLeaderBoard(): List<LeaderBoard>

    @GET("/languageLeaderBoard/{selectedLanguage}")
    suspend fun languageLeaderBoard(@Path("selectedLanguage") selectedLanguage: String): List<LeaderBoard>

    @GET("/hasAttemptedToday/{selectedLanguage}")
    suspend fun hasAttemptedToday(@Path("selectedLanguage") selectedLanguage: String): List<StreakContent>

    @GET("/lastThirtyDays/{username}")
    suspend fun lastThirtyDays(@Path("username") username: String): List<Boolean>

    @GET("/data/updateAll")
    suspend fun updateAll()

    @GET("/data/updateUser/{username}")
    suspend fun updateUser(@Path("username") username: String)

    @GET("/questionsSolved/{username}")
    suspend fun questionsSolved(@Path("username") username: String): List<String>

    @GET("/nameAndLanguage/{username}")
    suspend fun nameAndLanguage(@Path("username") username: String): List<String>

    @GET("/questionsCount/{selectedLanguage}")
    suspend fun questionsCount(@Path("selectedLanguage") selectedLanguage: String): List<Stats>

    @GET("/getUserSocials/{username}")
    suspend fun getUserSocials(@Path("username") username: String): Socials

    @GET("/getUserProfile/{username}")
    suspend fun getUserProfile(@Path("username") username: String): Socials

    @GET("/getContestInfo/{username}")
    suspend fun getContestInfo(@Path("username") username: String): Contest

    @GET("/isValidUser/{username}")
    suspend fun isValidUser(@Path("username") username: String): Map<String, String>

    @PUT("/editPassword/{id}")
    suspend fun editPassword(@Path("id") id: String, @Body data: EditPassword): String

    @PUT("/editDetails/{id}")
    suspend fun editDetails(@Path("id") id: String, @Body data: EditDetails): EditDetails
}