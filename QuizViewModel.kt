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
            Question(id = 1, title = "¿Qué palabra clave define una variable que NO cambia?", options = listOf("val", "var", "const", "let"), correctIndex = 0),
            Question(id = 2, title = "¿Para qué sirve el modificador 'Modifier' en Compose?", options = listOf("Cambiar aspecto/posición", "Guardar datos", "Borrar código", "Abrir la cámara"), correctIndex = 0),
            Question(id = 3, title = "¿Qué hace 'LaunchedEffect' en una pantalla?", options = listOf("Ejecuta código asíncrono", "Cambia el color", "Cierra la app", "Dibuja un botón"), correctIndex = 0),
            Question(id = 4, title = "¿Qué componente permite apilar elementos (uno sobre otro)?", options = listOf("Box", "Column", "Row", "Scaffold"), correctIndex = 0),
            Question(id = 5, title = "¿Cuál es la función principal de un ViewModel?", options = listOf("Gestionar datos de la UI", "Diseñar iconos", "Instalar la app", "Navegar en Google"), correctIndex = 0),
            Question(id = 6, title = "¿Qué operador se usa para llamadas seguras (null safety)?", options = listOf("?.", "!!", "?:", "=="), correctIndex = 0),
            Question(id = 7, title = "¿Qué hace 'mutableStateOf' en Compose?", options = listOf("Crea un estado que la UI observa", "Borra la memoria", "Detiene la app", "Suma números"), correctIndex = 0),
            Question(id = 8, title = "¿En qué carpeta se guardan los iconos de la app?", options = listOf("res/drawable", "res/values", "manifest", "bin"), correctIndex = 0),
            Question(id = 9, title = "¿Qué es una Corrutina en Kotlin?", options = listOf("Un hilo ligero asíncrono", "Una lista de números", "Un error del sistema", "Un tipo de botón"), correctIndex = 0),
            Question(id = 10, title = "¿Cuál es el archivo que contiene los permisos de Android?", options = listOf("AndroidManifest.xml", "build.gradle", "colors.xml", "MainActivity.kt"), correctIndex = 0),
            Question(id = 11, title = "¿Qué función se usa para mostrar una imagen?", options = listOf("Image()", "Icon()", "Picture()", "Bitmap()"), correctIndex = 0),
            Question(id = 12, title = "¿Para qué sirve 'LazyRow'?", options = listOf("Listas horizontales eficientes", "Listas verticales", "Cuadrículas", "Botones grandes"), correctIndex = 0),
            Question(id = 13, title = "¿Qué hace 'remember' en una función Composable?", options = listOf("Guarda un valor tras recomponer", "Reinicia el teléfono", "Borra el caché", "Envía un email"), correctIndex = 0),
            Question(id = 14, title = "¿Qué significa el estado 'onPause' en la Activity?", options = listOf("La app pierde el foco", "La app se borró", "La app está cargando", "La app se inició"), correctIndex = 0),
            Question(id = 15, title = "¿Qué herramienta usamos para manejar versiones de código?", options = listOf("Git / GitHub", "WhatsApp", "Excel", "Android Studio"), correctIndex = 0)

        )
    }
}