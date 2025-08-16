package com.pdm.quiz

// Esta classe representa o documento de um usuário na coleção "users"
data class User(
    val uid: String = "",
    val displayName: String = "",
    val totalScore: Long = 0
)