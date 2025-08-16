package com.pdm.quiz

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Anotação para permitir passar a lista de questões entre Activities
@Parcelize
data class QuizCategory(
    var id: String = "",
    var name: String = "",
    var questions: List<Question> = emptyList()
) : Parcelable

@Parcelize
data class Question(
    var id: String = "",
    var text: String = "",
    var options: List<String> = emptyList(),
    var correctIndex: Int = 0 // Agora usamos o índice da resposta correta
) : Parcelable