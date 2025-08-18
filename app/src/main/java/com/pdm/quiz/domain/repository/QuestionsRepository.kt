package com.pdm.quiz.domain.repository

import com.pdm.quiz.domain.model.Category
import com.pdm.quiz.domain.model.Question

interface QuestionsRepository {
    suspend fun syncAll(): Result<Unit>

    suspend fun getCategories(): Result<List<Category>>

    suspend fun getRandomQuestions(categoryId: String, limit: Int = 5): Result<List<Question>>
}
