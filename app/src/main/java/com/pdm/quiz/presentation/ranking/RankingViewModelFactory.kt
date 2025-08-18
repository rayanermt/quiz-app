package com.pdm.quiz.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdm.quiz.domain.repository.UsersRepository

class RankingViewModelFactory(private val usersRepo: UsersRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            return RankingViewModel(usersRepo) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
