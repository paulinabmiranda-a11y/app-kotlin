package com.example.app_kotlin.trivia

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        QuizUiState(questions = seedQuestions())
    )

    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onSelectedOption(index: Int) {
        val current = _uiState.value
        if (current.isFinished) return
        // No permitir cambiar opción si ya se confirmó (hay feedback)
        if (current.feedback != null) return
        _uiState.value = current.copy(selectedIndex = index)
    }

    fun onConfirmAnswer() {
        val current = _uiState.value
        val selected = current.selectedIndex ?: return
        val currentQuestion = current.currentQuestion ?: return

        val isCorrect = selected == currentQuestion.correctIndex
        val newScore = if (isCorrect) current.score + 100 else current.score
        val newLives = if (isCorrect) current.lives else current.lives - 1

        // Mostrar feedback — la navegación ocurre en onNextQuestion()
        _uiState.value = current.copy(
            score = newScore,
            lives = newLives,
            feedback = if (isCorrect) Feedback.CORRECT else Feedback.INCORRECT
        )
    }

    fun onNextQuestion() {
        val current = _uiState.value

        // Fin por vidas agotadas
        if (current.lives <= 0) {
            _uiState.value = current.copy(isFinished = true, feedback = null)
            return
        }

        val nextIndex = current.currentIndex + 1
        val finished = nextIndex >= current.questions.size

        _uiState.value = current.copy(
            currentIndex = nextIndex,
            selectedIndex = null,
            feedback = null,
            isFinished = finished
        )
    }

    private fun seedQuestions(): List<Question> {
        return listOf(
            Question(
                id = 1,
                title = "¿Qué palabra clave se usa para declarar una variable inmutable en Kotlin?",
                options = listOf("var", "val", "let", "const"),
                correctIndex = 1
            ),
            Question(
                id = 2,
                title = "En Jetpack Compose, ¿qué anotación marca una función como UI?",
                options = listOf("@UI", "@Widget", "@Composable", "@Compose"),
                correctIndex = 2
            ),
            Question(
                id = 3,
                title = "¿Qué componente se usa para listas eficientes y scrolleables en Compose?",
                options = listOf("Column", "RecyclerView", "Stack", "LazyColumn"),
                correctIndex = 3
            ),
            Question(
                id = 4,
                title = "La instrucción que permite restaurar estado tras recreación de Activity es:",
                options = listOf("intentData", "savedInstanceState", "activityState", "bundleConfig"),
                correctIndex = 1
            ),
            Question(
                id = 5,
                title = "¿Qué clase se usa como base para guardar estado que sobrevive cambios de configuración?",
                options = listOf("Activity", "Fragment", "ViewModel", "Repository"),
                correctIndex = 2
            ),
            Question(
                id = 6,
                title = "¿Qué función de Compose se usa para recordar un valor a través de recomposiciones?",
                options = listOf("rememberState()", "remember { }", "stateOf()", "mutableOf()"),
                correctIndex = 1
            ),
            Question(
                id = 7,
                title = "En Kotlin, ¿cómo se declara una función de extensión sobre String?",
                options = listOf("fun String.miFun()", "extend String miFun()", "fun miFun(String)", "String::miFun()"),
                correctIndex = 0
            ),
            Question(
                id = 8,
                title = "¿Qué operador en Kotlin lanza NullPointerException si el valor es null?",
                options = listOf("?.", "?:", "!!", "::"),
                correctIndex = 2
            ),
            Question(
                id = 9,
                title = "¿Qué composable se usa para apilar elementos uno encima del otro?",
                options = listOf("Column", "Row", "Box", "Scaffold"),
                correctIndex = 2
            ),
            Question(
                id = 10,
                title = "¿Qué clase de Kotlin representa un resultado que puede ser éxito o fallo?",
                options = listOf("Option", "Either", "Result", "Maybe"),
                correctIndex = 2
            ),
            Question(
                id = 11,
                title = "¿Qué modificador de Compose hace que un elemento ocupe todo el ancho disponible?",
                options = listOf("Modifier.expand()", "Modifier.fillMaxWidth()", "Modifier.matchParent()", "Modifier.wrapContent()"),
                correctIndex = 1
            ),
            Question(
                id = 12,
                title = "En Kotlin, ¿qué palabra clave se usa para heredar de una clase?",
                options = listOf("implements", "extends", ":", "inherits"),
                correctIndex = 2
            ),
            Question(
                id = 13,
                title = "¿Qué función de colecciones Kotlin transforma cada elemento de una lista?",
                options = listOf("filter", "map", "reduce", "forEach"),
                correctIndex = 1
            ),
            Question(
                id = 14,
                title = "¿Qué tipo de clase en Kotlin NO puede ser heredada por defecto?",
                options = listOf("abstract class", "open class", "class (sin modificador)", "sealed class"),
                correctIndex = 2
            ),
            Question(
                id = 15,
                title = "¿Cuál es el composable correcto para mostrar texto en Jetpack Compose?",
                options = listOf("Label()", "TextView()", "Text()", "TextWidget()"),
                correctIndex = 2
            )
        )
    }
}