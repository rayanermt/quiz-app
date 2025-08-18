package com.pdm.quiz.data.repository.impl

import com.pdm.quiz.data.remote.dto.UserDto
import com.pdm.quiz.data.remote.firestore.UsersRemoteDataSource
import com.pdm.quiz.domain.model.LeaderboardEntry
import com.pdm.quiz.domain.model.User
import com.pdm.quiz.domain.repository.UsersRepository

class UsersRepositoryImpl(
    private val usersRemote: UsersRemoteDataSource
) : UsersRepository {

    override suspend fun upsertAndGet(user: User): Result<User> = runCatching {
        usersRemote.upsertAndGet(user.toDto()).toDomain()
    }

    override suspend fun getByUid(uid: String): Result<User?> = runCatching {
        usersRemote.getByUid(uid)?.toDomain()
    }

    override suspend fun topLeaderboard(limit: Int) = runCatching {
        usersRemote.top(limit).map { it.toLeaderboard() }
    }

    private fun User.toDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs
    )

    private fun UserDto.toDomain() = User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs
    )

    private fun UserDto.toLeaderboard() = LeaderboardEntry(
        uid = uid,
        displayName = displayName,
        photoUrl = photoUrl,
        totalScore = totalScore
    )

}
