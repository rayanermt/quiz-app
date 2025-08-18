package com.pdm.quiz.trash.model

data class Answer(
    val questionId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val answeredAtMs: Long
)
