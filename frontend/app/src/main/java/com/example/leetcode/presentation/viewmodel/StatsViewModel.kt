package com.example.leetcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leetcode.domain.model.Stats
import com.example.leetcode.domain.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val statsRepository: StatsRepository) :
    ViewModel() {
    private val _statsDataMap = MutableStateFlow<Map<String, List<Stats>>>(emptyMap())
    val statsDataMap: StateFlow<Map<String, List<Stats>>> = _statsDataMap

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
                val java = statsRepository.questionsCount("Java")
                val cpp = statsRepository.questionsCount("C++")
                _statsDataMap.value = mapOf(
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
        _statsDataMap.value = emptyMap()
        isDataLoaded = false
        _isLoading.value = false
    }
}