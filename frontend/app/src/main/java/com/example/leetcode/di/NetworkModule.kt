package com.example.leetcode.di

import com.example.leetcode.data.repository.AuthRepositoryImpl
import com.example.leetcode.data.repository.HomeRepositoryImpl
import com.example.leetcode.data.repository.LeaderBoardRepositoryImpl
import com.example.leetcode.data.repository.SettingsRepositoryImpl
import com.example.leetcode.data.repository.StatsRepositoryImpl
import com.example.leetcode.data.repository.UserRepositoryImpl
import com.example.leetcode.domain.repository.AuthRepository
import com.example.leetcode.domain.repository.HomeRepository
import com.example.leetcode.domain.repository.LeaderBoardRepository
import com.example.leetcode.domain.repository.SettingsRepository
import com.example.leetcode.domain.repository.StatsRepository
import com.example.leetcode.domain.repository.UserRepository
import com.example.leetcode.domain.service.ApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    private const val BASE_URL =
        "https://8f288a449116.ngrok-free.app"

    @Provides
    @Singleton
    @Named("default")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(@Named("default") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideAuthRepository(
        apiService: ApiService,
    ): AuthRepository = AuthRepositoryImpl(apiService)

    @Provides
    fun provideHomeRepository(
        apiService: ApiService,
    ): HomeRepository = HomeRepositoryImpl(apiService)

    @Provides
    fun provideStatsRepository(
        apiService: ApiService,
    ): StatsRepository = StatsRepositoryImpl(apiService)

    @Provides
    fun provideLeaderBoardRepository(
        apiService: ApiService,
    ): LeaderBoardRepository = LeaderBoardRepositoryImpl(apiService)

    @Provides
    fun provideUserRepository(
        apiService: ApiService,
    ): UserRepository = UserRepositoryImpl(apiService)

    @Provides
    fun provideSettingsRepository(
        apiService: ApiService,
    ): SettingsRepository = SettingsRepositoryImpl(apiService)
}
