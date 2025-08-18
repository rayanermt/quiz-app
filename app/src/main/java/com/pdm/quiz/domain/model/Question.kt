package com.pdm.quiz.domain.model

data class Question(
    val id: String,
    val categoryId: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)