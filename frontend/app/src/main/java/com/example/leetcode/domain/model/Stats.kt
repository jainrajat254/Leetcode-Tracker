package com.example.leetcode.domain.model

data class Stats(
    val name: String,
    val submissionCalendar: List<Boolean>,
    val totalSolved: Int,
    val username: String,
    val year: String
)