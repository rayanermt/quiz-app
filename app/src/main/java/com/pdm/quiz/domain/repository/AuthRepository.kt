package com.pdm.quiz.domain.repository

import com.pdm.quiz.domain.model.User

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    fun currentUser(): User?
    fun signOut()
}