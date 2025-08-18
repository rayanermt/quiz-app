package com.pdm.quiz.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthDataSource(
    private val auth: FirebaseAuth
) {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser =
        suspendCancellableCoroutine { cont ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                val user = auth.currentUser
                if (task.isSuccessful && user != null) cont.resume(user)
                else cont.resumeWithException(task.exception ?: RuntimeException("Auth error"))
            }
        }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun signOut() = auth.signOut()
}
