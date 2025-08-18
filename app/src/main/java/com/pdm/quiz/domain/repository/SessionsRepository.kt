package com.pdm.quiz.domain.repository

import com.pdm.quiz.domain.model.SessionItem

interface SessionsRepository {
    suspend fun saveResult(
        categoryId: String,
        quizName: String,
        correct: Int,
        total: Int,
        durationMs: Long
    ): Result<Unit>

    suspend fun getUserHistory(): Result<List<SessionItem>>
}
