package com.pdm.quiz.domain.model

data class LeaderboardEntry(
    val uid: String,
    val displayName: String,
    val photoUrl: String?,
    val totalScore: Long
)
