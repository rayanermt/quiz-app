package com.pdm.quiz.data.remote.dto

data class QuestionDto(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val updatedAt: Long = 0L
)
