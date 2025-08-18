package com.pdm.quiz.trash.dto.firestore

data class QuizSessionDto(
    val id: String = "",
    val userId: String = "",
    val categoryId: String = "",
    val questionIds: List<String> = emptyList(),
    val chosenIndexes: List<Int?> = emptyList(),
    val correctCount: Int = 0,
    val startedAt: Long = 0L,
    val finishedAt: Long? = null
)
