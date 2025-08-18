package com.pdm.quiz.trash.dto.firestore

data class UserDto(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
