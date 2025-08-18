package com.pdm.quiz.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdm.quiz.domain.repository.QuestionsRepository
import com.pdm.quiz.domain.repository.SessionsRepository

class QuizViewModelFactory(
    private val questionsRepo: QuestionsRepository,
    private val sessionsRepo: SessionsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(questionsRepo, sessionsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
