package com.pdm.quiz.data.remote.dto

data class UserDto(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAtMs: Long = 0L,
    val updatedAtMs: Long = 0L,
    val totalScore: Long = 0L
)
