package com.pdm.quiz.trash.model

data class Quiz(
    val id: String,
    val title: String,
    val description: String?,
    val category: String?,
    val timeLimitSec: Int?,
    val version: Int,
    val updatedAt: Long
)
