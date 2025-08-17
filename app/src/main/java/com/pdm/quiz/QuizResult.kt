package com.pdm.quiz

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Use "data class" para indicar que esta classe serve para guardar dados.
data class QuizResult(
    val quizId: String = "",
    val quizName: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Int = 0,
    // Esta anotação especial faz o Firebase preencher a data e hora do servidor
    // automaticamente quando o resultado é salvo.
    @ServerTimestamp
    val completedAt: Date? = null
)