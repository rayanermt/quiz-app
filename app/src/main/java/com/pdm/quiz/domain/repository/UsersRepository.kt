package com.pdm.quiz.domain.repository

import com.pdm.quiz.domain.model.LeaderboardEntry
import com.pdm.quiz.domain.model.User

interface UsersRepository {
    suspend fun upsertAndGet(user: User): Result<User>
    suspend fun getByUid(uid: String): Result<User?>
    suspend fun topLeaderboard(limit: Int = 20): Result<List<LeaderboardEntry>>
}