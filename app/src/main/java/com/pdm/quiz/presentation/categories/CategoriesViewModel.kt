package com.pdm.quiz.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.quiz.domain.model.Category
import com.pdm.quiz.domain.repository.AuthRepository
import com.pdm.quiz.domain.repository.QuestionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repo: QuestionsRepository,
    private val auth: AuthRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val categories: List<Category> = emptyList(),
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _ui.value = UiState(loading = true)
        repo.syncAll()
        val res = repo.getCategories()
        _ui.value = res.fold(
            onSuccess = { UiState(loading = false, categories = it) },
            onFailure = { UiState(loading = false, error = it.message) }
        )
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            auth.signOut()
        }
    }

}
