// data/remote/firestore/SessionsRemoteDataSource.kt
package com.pdm.quiz.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.pdm.quiz.data.remote.dto.QuizResultDto
import com.pdm.quiz.utils.getMillis
import kotlinx.coroutines.tasks.await

class SessionsRemoteDataSource(private val db: FirebaseFirestore) {
    private fun users() = db.collection("users")

    suspend fun addResult(uid: String, dto: QuizResultDto) {
        val data = hashMapOf(
            "categoryId" to dto.categoryId,
            "quizName" to dto.quizName,
            "correctCount" to dto.correctCount,
            "total" to dto.total,
            "durationMs" to dto.durationMs,
            "completedAt" to FieldValue.serverTimestamp()
        )
        users().document(uid).collection("quiz_history").add(data).await()
    }

    suspend fun incrementTotalScore(uid: String, delta: Long) {
        users().document(uid)
            .set(mapOf("totalScore" to FieldValue.increment(delta)), SetOptions.merge())
            .await()
    }

    suspend fun history(uid: String): List<QuizResultDto> {
        val snap = users().document(uid)
            .collection("quiz_history")
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .get().await()

        return snap.documents.map { d ->
            QuizResultDto(
                categoryId = d.getString("categoryId") ?: "",
                quizName = d.getString("quizName") ?: "",
                correctCount = (d.getLong("correctCount") ?: 0L).toInt(),
                total = (d.getLong("total") ?: 0L).toInt(),
                durationMs = d.getLong("durationMs") ?: 0L,
                completedAtMs = d.getMillis("completedAt")
            )
        }
    }
}
