package com.pdm.quiz.domain.model

data class SessionItem(
    val quizName: String,
    val correctCount: Int,
    val total: Int,
    val durationMs: Long,
    val completedAtMs: Long
)
