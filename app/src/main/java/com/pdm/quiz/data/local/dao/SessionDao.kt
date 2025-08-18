package com.pdm.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pdm.quiz.data.local.entity.QuizSessionEntity

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: QuizSessionEntity)

    @Query("SELECT * FROM quiz_session WHERE userId = :uid ORDER BY endedAtMs DESC")
    suspend fun history(uid: String): List<QuizSessionEntity>
}
