package com.pdm.quiz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val text: String,
    val optionsJson: String,
    val correctIndex: Int,
    val updatedAt: Long = 0L
)
