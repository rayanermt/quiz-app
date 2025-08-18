package com.pdm.quiz.data.remote.dto

data class QuizResultDto(
    val categoryId: String = "",
    val quizName: String = "",
    val correctCount: Int = 0,
    val total: Int = 0,
    val durationMs: Long = 0L,
    val completedAtMs: Long = 0L
)
