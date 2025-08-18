package com.pdm.quiz.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.quiz.domain.model.Question
import com.pdm.quiz.domain.repository.QuestionsRepository
import com.pdm.quiz.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val questionsRepo: QuestionsRepository,
    private val sessionsRepo: SessionsRepository,
    private val nowMs: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    data class Ui(
        val loading: Boolean = true,
        val quizName: String = "",
        val categoryId: String = "",
        val index: Int = 0,
        val total: Int = 0,
        val score: Int = 0,
        val question: Question? = null,
        val finished: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui

    private var questions: List<Question> = emptyList()

    private var startAt: Long = 0L

    fun init(categoryId: String, quizName: String) {
        _ui.value = Ui(loading = true, categoryId = categoryId, quizName = quizName)
        viewModelScope.launch {
            val res = questionsRepo.getRandomQuestions(categoryId, 5)
            res.onSuccess { list ->
                questions = list
                startAt = nowMs()
                _ui.value = _ui.value.copy(
                    loading = false,
                    index = 0,
                    total = list.size,
                    question = list.firstOrNull()
                )
            }.onFailure { e ->
                _ui.value = _ui.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun answer(selectedIndex: Int) {
        val ui = _ui.value
        val q = questions.getOrNull(ui.index) ?: return
        val add = if (selectedIndex == q.correctIndex) 1 else 0
        val newScore = ui.score + add
        val next = ui.index + 1

        if (next >= questions.size) {
            _ui.value = ui.copy(score = newScore, finished = true)
            viewModelScope.launch {
                val duration = nowMs() - startAt
                sessionsRepo.saveResult(
                    categoryId = ui.categoryId,
                    quizName = ui.quizName,
                    correct = newScore,
                    total = questions.size,
                    durationMs = duration
                )
            }
        } else {
            _ui.value = ui.copy(
                index = next,
                score = newScore,
                question = questions[next]
            )
        }
    }
}
