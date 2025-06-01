package com.example.leetcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leetcode.domain.model.LeaderBoard
import com.example.leetcode.domain.repository.LeaderBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val leaderBoardRepository: LeaderBoardRepository,
) : ViewModel() {

    // Language leaderboard data (grouped by language)
    private val _langLBDataMap = MutableStateFlow<Map<String, List<LeaderBoard>>>(emptyMap())
    val langLBDataMap: StateFlow<Map<String, List<LeaderBoard>>> = _langLBDataMap

    // Club leaderboard data (all members)
    private val _clubLBData = MutableStateFlow<List<LeaderBoard>>(emptyList())
    val clubLBData: StateFlow<List<LeaderBoard>> = _clubLBData

    // Loading states
    private val _isLoadingLangLB = MutableStateFlow(false)
    val isLoadingLangLB: StateFlow<Boolean> = _isLoadingLangLB

    private val _isLoadingClubLB = MutableStateFlow(false)
    val isLoadingClubLB: StateFlow<Boolean> = _isLoadingClubLB

    // Track loaded states separately
    private var isLangDataLoaded = false
    private var isClubDataLoaded = false

    fun loadDataIfNotLoadedForLangLB() {
        if (!isLangDataLoaded && !_isLoadingLangLB.value) {
            refreshLanguageLeaderBoard()
        }
    }

    fun loadDataIfNotLoadedForClubLB() {
        if (!isClubDataLoaded && !_isLoadingClubLB.value) {
            refreshClubLeaderBoard()
        }
    }

    fun refreshLanguageLeaderBoard() {
        _isLoadingLangLB.value = true
        viewModelScope.launch {
            try {
                val java = leaderBoardRepository.languageLeaderBoard("Java")
                val cpp = leaderBoardRepository.languageLeaderBoard("C++")
                _langLBDataMap.value = mapOf(
                    "Java" to java,
                    "C++" to cpp
                )
                isLangDataLoaded = true
            } catch (e: Exception) {
                // handle error (you might want to update an error state here)
            } finally {
                _isLoadingLangLB.value = false
            }
        }
    }

    fun refreshClubLeaderBoard() {
        _isLoadingClubLB.value = true
        viewModelScope.launch {
            try {
                val allMembers = leaderBoardRepository.clubLeaderBoard()
                _clubLBData.value = allMembers
                isClubDataLoaded = true
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoadingClubLB.value = false
            }
        }
    }

    fun clear() {
        _langLBDataMap.value = emptyMap()
        _clubLBData.value = emptyList()
        isLangDataLoaded = false
        isClubDataLoaded = false
    }
}