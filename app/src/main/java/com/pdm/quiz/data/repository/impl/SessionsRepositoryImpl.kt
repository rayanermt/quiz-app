package com.pdm.quiz.data.repository.impl

import com.pdm.quiz.data.local.dao.SessionDao
import com.pdm.quiz.data.local.entity.QuizSessionEntity
import com.pdm.quiz.data.remote.firestore.SessionsRemoteDataSource
import com.pdm.quiz.domain.model.SessionItem
import com.pdm.quiz.domain.repository.SessionsRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class SessionsRepositoryImpl(
    private val auth: FirebaseAuth,
    private val sessionDao: SessionDao,
    private val remote: SessionsRemoteDataSource
) : SessionsRepository {

    override suspend fun saveResult(
        categoryId: String,
        quizName: String,
        correct: Int,
        total: Int,
        durationMs: Long
    ): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No user logged")
        val now = System.currentTimeMillis()

        val entity = QuizSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = uid,
            categoryId = categoryId,
            quizName = quizName,
            startedAtMs = now - durationMs,
            endedAtMs = now,
            correctCount = correct,
            total = total,
            durationMs = durationMs
        )
        sessionDao.insert(entity)

        remote.addResult(
            uid, com.pdm.quiz.data.remote.dto.QuizResultDto(
                categoryId = categoryId,
                quizName = quizName,
                correctCount = correct,
                total = total,
                durationMs = durationMs,
                completedAtMs = now
            )
        )

        remote.incrementTotalScore(uid, correct.toLong())
    }

    override suspend fun getUserHistory(): Result<List<SessionItem>> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No user logged")
        sessionDao.history(uid).map {
            SessionItem(
                quizName = it.quizName,
                correctCount = it.correctCount,
                total = it.total,
                durationMs = it.durationMs,
                completedAtMs = it.endedAtMs
            )
        }
    }
}
