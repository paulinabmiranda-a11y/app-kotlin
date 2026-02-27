package com.example.app_kotlin.trivia

data class Question(
    val id: Int,
    val title: String,
    val options: List<String>,
    val correctIndex: Int
)

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val lives: Int = 3,                      // ❤️ Vidas restantes
    val feedback: Feedback? = null,          // Feedback tras confirmar
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentIndex)

    val isLastQuestion: Boolean
        get() = currentIndex == questions.size - 1
}

enum class Feedback { CORRECT, INCORRECT }