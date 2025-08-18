package com.pdm.quiz.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.quiz.domain.model.LeaderboardEntry
import com.pdm.quiz.domain.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel(private val usersRepo: UsersRepository) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val items: List<LeaderboardEntry> = emptyList(),
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _ui.value = UiState(loading = true)
        val res = usersRepo.topLeaderboard(20)
        _ui.value = res.fold(
            onSuccess = { UiState(loading = false, items = it) },
            onFailure = { UiState(loading = false, error = it.message) }
        )
    }
}
