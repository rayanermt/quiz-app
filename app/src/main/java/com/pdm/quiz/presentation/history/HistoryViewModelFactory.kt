package com.pdm.quiz.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdm.quiz.domain.repository.SessionsRepository

class HistoryViewModelFactory(private val repo: SessionsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
