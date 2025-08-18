package com.pdm.quiz.trash.model

enum class Difficulty(val label: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    fun fromStringOrNull(value: String): Difficulty? =
        Difficulty.entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        }
}
