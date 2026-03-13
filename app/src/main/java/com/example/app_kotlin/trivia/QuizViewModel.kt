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
    // Esto revisa si estamos en la pregunta 15 (índice 14)
    val textoDelBoton: String
        get() = if (_uiState.value.currentIndex == 14) "Ver resultados" else "Confirmar"
    // Calcula el avance de 0 a 100
    val porcentajeAvance: Int
        get() = ((_uiState.value.currentIndex + 1) * 100) / 15

    fun onSelectedOption(index: Int) {
        val current = _uiState.value
        if (current.isFinished) return
        // No permitir cambiar opción si ya se confirmó (hay feedback)
        if (current.feedback != null) return
        _uiState.value = current.copy(selectedIndex = index)
    }

    fun onConfirmAnswer() {
        val current = _uiState.value
        // Si el alumno no ha seleccionado nada, no hacemos nada
        val selected = current.selectedIndex ?: return
        val currentQuestion = current.currentQuestion ?: return

        // 1. Revisamos si la respuesta es correcta
        val esCorrecta = selected == currentQuestion.correctIndex

        // 2. Calculamos las vidas: Si falló, le quitamos una (lives - 1)
        val vidasNuevas = if (esCorrecta) current.lives else current.lives - 1

        // 3. Calculamos el puntaje (opcional, pero sirve)
        val puntajeNuevo = if (esCorrecta) current.score + 100 else current.score

        // 4. Actualizamos la "pizarra" (el estado) con la nueva info
        _uiState.value = current.copy(
            score = puntajeNuevo,
            lives = vidasNuevas,
            feedback = if (esCorrecta) Feedback.CORRECT else Feedback.INCORRECT
        )

        // 5. Si se quedó sin vidas, marcamos que el juego terminó (Punto 3)
        if (vidasNuevas <= 0) {
            _uiState.value = _uiState.value.copy(isFinished = true)
        }
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
                title = "¿Cuál es la principal ventaja de usar Jetpack Compose?",
                options = listOf("Usa XML para todo", "Es declarativo y moderno", "Solo sirve para juegos", "Es más antiguo que Views"),
                correctIndex = 1
            ),
            Question(
                id = 2,
                title = "¿Qué palabra clave define una variable que NO puede cambiar (inmutable)?",
                options = listOf("var", "const", "val", "fixed"),
                correctIndex = 2
            ),
            Question(
                id = 3,
                title = "¿Para qué sirve la anotación @Composable?",
                options = listOf("Para crear funciones de UI", "Para guardar datos", "Para hacer llamadas a red", "Para borrar archivos"),
                correctIndex = 0
            ),
            Question(
                id = 4,
                title = "En Kotlin, ¿cómo se define una función?",
                options = listOf("void miFuncion()", "function miFuncion()", "def miFuncion()", "fun miFuncion()"),
                correctIndex = 3
            ),
            Question(
                id = 5,
                title = "¿Qué componente se usa para mostrar elementos uno debajo de otro?",
                options = listOf("Row", "Box", "Column", "Scaffold"),
                correctIndex = 2
            ),
            Question(
                id = 6,
                title = "¿Qué hace el modificador Modifier.fillMaxSize()?",
                options = listOf("Cambia el color", "Ocupa todo el espacio disponible", "Pone un borde", "Esconde el elemento"),
                correctIndex = 1
            ),
            Question(
                id = 7,
                title = "¿Para qué sirve el componente 'LazyColumn'?",
                options = listOf("Para listas cortas", "Para listas largas y eficientes", "Para mostrar una sola imagen", "Para botones"),
                correctIndex = 1
            ),
            Question(
                id = 8,
                title = "¿Qué significa el concepto de 'State' en Compose?",
                options = listOf("El nombre de la app", "Un valor que al cambiar actualiza la UI", "El color del icono", "La versión de Android"),
                correctIndex = 1
            ),
            Question(
                id = 9,
                title = "¿Cómo se llama el lenguaje oficial para desarrollar en Android?",
                options = listOf("Java", "Kotlin", "Python", "Swift"),
                correctIndex = 1
            ),
            Question(
                id = 10,
                title = "¿Qué componente permite apilar elementos uno sobre otro (como capas)?",
                options = listOf("Column", "Row", "Box", "Spacer"),
                correctIndex = 2
            ),
            Question(
                id = 11,
                title = "¿Cuál es el símbolo del operador 'Elvis' en Kotlin?",
                options = listOf("!!", "?.", "?:", "&&"),
                correctIndex = 2
            ),
            Question(
                id = 12,
                title = "¿Qué función se usa para recordar un valor entre recomposiciones?",
                options = listOf("save()", "remember { }", "store()", "keep()"),
                correctIndex = 1
            ),
            Question(
                id = 13,
                title = "¿Cuál es la función del ViewModel?",
                options = listOf("Dibujar botones", "Gestionar la lógica y datos", "Cambiar el fondo", "Cerrar la app"),
                correctIndex = 1
            ),
            Question(
                id = 14,
                title = "¿Qué componente se usa para mostrar texto en pantalla?",
                options = listOf("Label()", "TextField()", "Text()", "Typography()"),
                correctIndex = 2
            ),
            Question(
                id = 15,
                title = "¿Qué permite hacer el componente Scaffold?",
                options = listOf("Estructurar la pantalla (TopBar, etc)", "Hacer cálculos", "Cambiar el icono", "Solo poner colores"),
                correctIndex = 0
            )
        )
    }

    fun onRestartQuiz() {
        _uiState.value = QuizUiState(
            questions = seedQuestions(),
            currentIndex = 0,
            score = 0,
            lives = 3,
            isFinished = false,
            feedback = null,
            selectedIndex = null
        )
    }
}