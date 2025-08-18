package com.pdm.quiz.trash.model

import com.pdm.quiz.domain.model.Category

data class QuizResult(
    val sessionId: String,
    val userId: String,
    val category: Category,
    val totalQuestions: Int,
    val totalCorrect: Int,
    val durationMs: Long,
    val finishedAtMs: Long
)
