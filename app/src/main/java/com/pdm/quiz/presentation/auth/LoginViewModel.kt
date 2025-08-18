package com.pdm.quiz.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.quiz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        object Success : UiState
        data class Error(val message: String) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun signIn(idToken: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val res = authRepository.signInWithGoogle(idToken)
            _state.value = res.fold(
                onSuccess = { UiState.Success },
                onFailure = { UiState.Error(it.message ?: "Falha na autenticação") }
            )
        }
    }
}
