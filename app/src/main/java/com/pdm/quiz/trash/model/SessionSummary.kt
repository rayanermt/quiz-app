package com.pdm.quiz.trash.model

import com.pdm.quiz.domain.model.Category

data class SessionSummary(
    val sessionId: String,
    val userId: String,
    val category: Category,
    val startedAt: Long,
    val finishedAt: Long,
    val totalQuestions: Int,
    val totalCorrect: Int,
    val duration: Long
)
