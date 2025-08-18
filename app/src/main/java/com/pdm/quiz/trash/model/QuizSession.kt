package com.pdm.quiz.trash.model

import com.pdm.quiz.domain.model.Category

data class QuizSession(
    val sessionId: String,
    val userId: String,
    val category: Category,
    val questionIds: List<String>,
    val startedAt: Long,
    val finishedAt: Long?,
    val answers: List<Answer> = emptyList()
)
