package com.pdm.quiz.domain.model

data class User (
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val createdAtMs: Long,
    val updatedAtMs: Long
)
