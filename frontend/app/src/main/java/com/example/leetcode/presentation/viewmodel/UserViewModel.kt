package com.example.leetcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leetcode.domain.model.Contest
import com.example.leetcode.domain.model.Socials
import com.example.leetcode.domain.repository.UserRepository
import com.example.leetcode.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userSocials = MutableStateFlow<ResultState<Socials>>(ResultState.Idle)
    val userSocials: StateFlow<ResultState<Socials>> = _userSocials

    private val _contestInfo = MutableStateFlow<ResultState<Contest>>(ResultState.Idle)
    val contestInfo: StateFlow<ResultState<Contest>> = _contestInfo

    private val _solvedQuestions = MutableStateFlow<ResultState<List<String>>>(ResultState.Idle)
    val solvedQuestions: StateFlow<ResultState<List<String>>> = _solvedQuestions

    private val _activityStreak = MutableStateFlow<ResultState<List<Boolean>>>(ResultState.Idle)
    val activityStreak: StateFlow<ResultState<List<Boolean>>> = _activityStreak

    private val _userProfile = MutableStateFlow<ResultState<Socials>>(ResultState.Idle)
    val userProfile: StateFlow<ResultState<Socials>> = _userProfile

    private val _userInfo = MutableStateFlow<ResultState<List<String>>>(ResultState.Idle)
    val userInfo: StateFlow<ResultState<List<String>>> = _userInfo

    // Individual fetch functions

    fun getUserSocials(username: String) {
        viewModelScope.launch {
            _userSocials.value = ResultState.Loading
            try {
                val result = userRepository.getUserSocials(username)
                _userSocials.value = ResultState.Success(result)
            } catch (e: Exception) {
                _userSocials.value = ResultState.Error(e)
            }
        }
    }

    fun getContestInfo(username: String) {
        viewModelScope.launch {
            _contestInfo.value = ResultState.Loading
            try {
                val result = userRepository.getContestInfo(username)
                _contestInfo.value = ResultState.Success(result)
            } catch (e: Exception) {
                _contestInfo.value = ResultState.Error(e)
            }
        }
    }

    fun questionsSolved(username: String) {
        viewModelScope.launch {
            _solvedQuestions.value = ResultState.Loading
            try {
                val result = userRepository.questionsSolved(username)
                _solvedQuestions.value = ResultState.Success(result)
            } catch (e: Exception) {
                _solvedQuestions.value = ResultState.Error(e)
            }
        }
    }

    fun getActivityStreak(username: String) {
        viewModelScope.launch {
            _activityStreak.value = ResultState.Loading
            try {
                val result = userRepository.lastThirtyDays(username)
                _activityStreak.value = ResultState.Success(result)
            } catch (e: Exception) {
                _activityStreak.value = ResultState.Error(e)
            }
        }
    }

    fun getUserProfile(username: String) {
        viewModelScope.launch {
            _userProfile.value = ResultState.Loading
            try {
                val result = userRepository.getUserProfile(username)
                _userProfile.value = ResultState.Success(result)
            } catch (e: Exception) {
                _userProfile.value = ResultState.Error(e)
            }
        }
    }

    fun getNameAndLanguage(username: String) {
        viewModelScope.launch {
            _userInfo.value = ResultState.Loading
            try {
                val result = userRepository.nameAndLanguage(username)
                _userInfo.value = ResultState.Success(result)
            } catch (e: Exception) {
                _userInfo.value = ResultState.Error(e)
            }
        }
    }

    // Optional: function to call all at once
    fun getUserData(username: String) {
        viewModelScope.launch {
            val userSocialsDeferred = async { getUserSocials(username) }
            val contestInfoDeferred = async { getContestInfo(username) }
            val solvedQuestionsDeferred = async { questionsSolved(username) }
            val activityStreakDeferred = async { getActivityStreak(username) }
            val userProfileDeferred = async { getUserProfile(username) }

            userSocialsDeferred.await()
            contestInfoDeferred.await()
            solvedQuestionsDeferred.await()
            activityStreakDeferred.await()
            userProfileDeferred.await()
        }
    }

    fun clear() {
        _userSocials.value = ResultState.Idle
        _contestInfo.value = ResultState.Idle
        _solvedQuestions.value = ResultState.Idle
        _activityStreak.value = ResultState.Idle
        _userProfile.value = ResultState.Idle
        _userInfo.value = ResultState.Idle
    }
}
