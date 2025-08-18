package com.pdm.quiz.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm.quiz.data.remote.dto.UserDto
import kotlinx.coroutines.tasks.await

class UsersRemoteDataSource(
    private val db: FirebaseFirestore
) {
    private fun users() = db.collection("users")

    suspend fun upsertAndGet(user: UserDto): UserDto {
        val docRef = users().document(user.uid)
        return db.runTransaction { txn ->
            val snap = txn.get(docRef)
            val nowUser = if (snap.exists()) {
                val old = snap.toObject(UserDto::class.java)
                user.copy(
                    createdAtMs = old?.createdAtMs ?: user.createdAtMs
                )
            } else {
                user
            }
            txn.set(docRef, nowUser)
            nowUser
        }.await()
    }

    suspend fun getByUid(uid: String): UserDto? {
        val snap = users().document(uid).get().await()
        return if (snap.exists()) snap.toObject(UserDto::class.java) else null
    }

    suspend fun top(limit: Int): List<UserDto> {
        val snap = users()
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get().await()
        return snap.documents.mapNotNull { it.toObject(UserDto::class.java) }
    }
}
