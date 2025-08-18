package com.pdm.quiz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_session")
data class QuizSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val categoryId: String,
    val quizName: String,
    val startedAtMs: Long,
    val endedAtMs: Long,
    val correctCount: Int,
    val total: Int,
    val durationMs: Long
)
