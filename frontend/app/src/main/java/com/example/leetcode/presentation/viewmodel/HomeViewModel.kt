package com.example.leetcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leetcode.domain.model.StreakContent
import com.example.leetcode.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _streakDataMap = MutableStateFlow<Map<String, List<StreakContent>>>(emptyMap())
    val streakDataMap: StateFlow<Map<String, List<StreakContent>>> = _streakDataMap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var isDataLoaded = false

    fun loadDataIfNotLoaded() {
        if (!isDataLoaded && !isLoading.value) {
            refreshAll()
        }
    }

    fun refreshAll() {
        isDataLoaded = false
        _isLoading.value = true
        viewModelScope.launch {
            try {
                homeRepository.updateAll()
                val java = homeRepository.hasAttemptedToday("Java")
                val cpp = homeRepository.hasAttemptedToday("C++")
                _streakDataMap.value = mapOf(
                    "Java" to java,
                    "C++" to cpp
                )
                isDataLoaded = true
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clear() {
        _streakDataMap.value = emptyMap()
        isDataLoaded = false
    }
}

