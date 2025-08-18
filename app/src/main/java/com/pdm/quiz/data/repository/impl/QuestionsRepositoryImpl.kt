package com.pdm.quiz.data.repository.impl

import com.pdm.quiz.data.local.dao.CategoryDao
import com.pdm.quiz.data.local.dao.QuestionDao
import com.pdm.quiz.data.mapper.toDomain
import com.pdm.quiz.data.mapper.toEntity
import com.pdm.quiz.data.remote.firestore.QuestionsRemoteDataSource
import com.pdm.quiz.domain.model.Category
import com.pdm.quiz.domain.model.Question
import com.pdm.quiz.domain.repository.QuestionsRepository

class QuestionsRepositoryImpl(
    private val remote: QuestionsRemoteDataSource,
    private val categoryDao: CategoryDao,
    private val questionDao: QuestionDao
) : QuestionsRepository {

    override suspend fun syncAll(): Result<Unit> = runCatching {
        val categories = remote.categories()
        categoryDao.upsertAll(categories.map { it.toEntity() })

        categories.forEach { cat ->
            val qs = remote.questions(cat.id)
            val es = qs.map { it.toEntity(cat.id) }
            questionDao.upsertAll(es)
            android.util.Log.d("Sync", "Room: salvou ${es.size} perguntas para '${cat.id}'")
        }
    }

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        categoryDao.getAll().map { it.toDomain() }
    }

    override suspend fun getRandomQuestions(categoryId: String, limit: Int): Result<List<Question>> = runCatching {
        val all = questionDao.byCategory(categoryId).map { it.toDomain() }
        all.shuffled().take(limit)
    }
}
