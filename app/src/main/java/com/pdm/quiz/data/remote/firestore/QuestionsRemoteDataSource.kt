package com.pdm.quiz.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.quiz.data.remote.dto.CategoryDto
import com.pdm.quiz.data.remote.dto.QuestionDto
import com.pdm.quiz.utils.getMillis
import kotlinx.coroutines.tasks.await

class QuestionsRemoteDataSource(private val db: FirebaseFirestore) {

    suspend fun categories(): List<CategoryDto> {
        val snap = db.collection("categories").get().await()
        return snap.documents.map { doc ->
            CategoryDto(
                id = doc.id,
                name = doc.getString("name") ?: doc.id
            )
        }
    }

    suspend fun questions(categoryId: String): List<QuestionDto> {
        val snap = db.collection("categories")
            .document(categoryId)
            .collection("questions")
            .get().await()

        return snap.documents.map { doc ->
            val options = (doc.get("options") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            val correctIndex = (doc.getLong("correctIndex") ?: 0L).toInt()
            QuestionDto(
                id = doc.id,
                text = doc.getString("text") ?: "",
                options = options,
                correctIndex = correctIndex,
                updatedAt = doc.getMillis("updatedAt") // <- Timestamp -> ms
            )
        }
    }
}
