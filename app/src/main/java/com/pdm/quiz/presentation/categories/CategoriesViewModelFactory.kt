package com.pdm.quiz.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdm.quiz.domain.repository.AuthRepository
import com.pdm.quiz.domain.repository.QuestionsRepository

class CategoriesViewModelFactory(
    private val repo: QuestionsRepository,
    private val auth: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(repo, auth) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
