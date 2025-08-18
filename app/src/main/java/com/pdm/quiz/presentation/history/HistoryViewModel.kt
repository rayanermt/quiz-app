package com.pdm.quiz.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.quiz.domain.model.SessionItem
import com.pdm.quiz.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val repo: SessionsRepository) : ViewModel() {
    data class Ui(val loading: Boolean = true, val items: List<SessionItem> = emptyList(), val error: String? = null)
    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _ui.value = Ui(loading = true)
        val res = repo.getUserHistory()
        _ui.value = res.fold(
            onSuccess = { Ui(loading = false, items = it) },
            onFailure = { Ui(loading = false, error = it.message) }
        )
    }
}
