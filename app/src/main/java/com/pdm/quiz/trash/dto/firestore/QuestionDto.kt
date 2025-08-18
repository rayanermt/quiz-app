package com.pdm.quiz.trash.dto.firestore

data class QuestionDto(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val difficulty: String = "medium",
    val tags: List<String> = emptyList(),
    val active: Boolean = true,
    val rand: Double = 0.0,
    val updatedAt: Long = 0L
)
