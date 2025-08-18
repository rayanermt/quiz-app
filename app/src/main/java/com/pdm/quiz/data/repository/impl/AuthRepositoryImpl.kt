package com.pdm.quiz.data.repository.impl

import com.pdm.quiz.data.remote.auth.FirebaseAuthDataSource
import com.pdm.quiz.domain.model.User
import com.pdm.quiz.domain.repository.AuthRepository
import com.pdm.quiz.domain.repository.UsersRepository

class AuthRepositoryImpl(
    private val authDs: FirebaseAuthDataSource,
    private val usersRepo: UsersRepository
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val fu = authDs.signInWithGoogle(idToken)
        val now = System.currentTimeMillis()
        val createdFromMeta = fu.metadata?.creationTimestamp ?: now
        val updatedFromMeta = fu.metadata?.lastSignInTimestamp ?: now

        val base = User(
            uid = fu.uid,
            email = fu.email ?: "",
            displayName = fu.displayName ?: "Usuário",
            photoUrl = fu.photoUrl?.toString(),
            createdAtMs = createdFromMeta,
            updatedAtMs = now.coerceAtLeast(updatedFromMeta) // garante monotonicidade
        )
        usersRepo.upsertAndGet(base).getOrThrow()
    }

    override fun currentUser(): User? = authDs.currentUser()?.let { fu ->
        val now = System.currentTimeMillis()
        User(
            uid = fu.uid,
            email = fu.email ?: "",
            displayName = fu.displayName ?: "Usuário",
            photoUrl = fu.photoUrl?.toString(),
            createdAtMs = fu.metadata?.creationTimestamp ?: 0L,
            updatedAtMs = fu.metadata?.lastSignInTimestamp ?: now
        )
    }

    override fun signOut() = authDs.signOut()
}
