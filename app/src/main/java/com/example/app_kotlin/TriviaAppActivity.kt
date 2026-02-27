package com.example.app_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_kotlin.trivia.Feedback
import com.example.app_kotlin.trivia.QuizUiState
import com.example.app_kotlin.trivia.QuizViewModel
import com.example.app_kotlin.ui.theme.AppkotlinTheme

class TriviaAppActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppkotlinTheme {
                val viewModel: QuizViewModel = viewModel()
                val state = viewModel.uiState.collectAsStateWithLifecycle().value

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Trivia App", color = Color.White) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF1E88E5)
                            )
                        )
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        if (state.isFinished) {
                            FinishedScreen(
                                score = state.score,
                                total = state.questions.size * 100,
                                livesLeft = state.lives
                            )
                        } else {
                            QuestionScreen(
                                state = state,
                                onSelectedOption = viewModel::onSelectedOption,
                                onConfirm = viewModel::onConfirmAnswer,
                                onNext = viewModel::onNextQuestion
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionScreen(
    state: QuizUiState,
    onSelectedOption: (Int) -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit,
) {
    val q = state.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Fila superior: progreso + vidas ──────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pregunta ${state.currentIndex + 1} de ${state.questions.size}",
                style = MaterialTheme.typography.titleMedium
            )
            // Vidas: muestra ❤️ por cada vida restante
            val heartsDisplay = "❤️".repeat(state.lives) + "🖤".repeat(3 - state.lives)
            Text(
                text = heartsDisplay,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // ── Título de la pregunta ─────────────────────────────────────────
        Text(
            text = q.title,
            style = MaterialTheme.typography.headlineSmall
        )

        // ── Opciones ──────────────────────────────────────────────────────
        q.options.forEachIndexed { index, option ->
            val isSelected = state.selectedIndex == index

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectedOption(index) },
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isSelected) 14.dp else 1.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectedOption(index) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // ── Feedback ──────────────────────────────────────────────────────
        if (state.feedback != null) {
            val (emoji, msg, color) = when (state.feedback) {
                Feedback.CORRECT -> Triple("✅", "¡Correcto!", Color(0xFF388E3C))
                Feedback.INCORRECT -> Triple("❌", "Incorrecto", Color(0xFFC62828))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "$emoji $msg",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Botón principal ───────────────────────────────────────────────
        if (state.feedback == null) {
            // Aún no confirmó → mostrar Confirmar
            Button(
                onClick = onConfirm,
                enabled = state.selectedIndex != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        } else {
            // Ya confirmó → avanzar
            val buttonLabel = if (state.isLastQuestion || state.lives <= 0) "Ver resultados" else "Siguiente"
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonLabel)
            }
        }

        // ── Porcentaje de avance ──────────────────────────────────────────
        val progress = ((state.currentIndex + 1).toFloat() / state.questions.size * 100).toInt()
        Text(
            text = "Porcentaje de avance: $progress%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun FinishedScreen(
    score: Int,
    total: Int,
    livesLeft: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val title = if (livesLeft <= 0) "¡Sin vidas! 💀" else "¡Quiz finalizado! 🎉"
        Text(text = title, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        val heartsDisplay = "❤️".repeat(livesLeft) + "🖤".repeat(3 - livesLeft)
        Text(text = heartsDisplay, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu puntaje: $score / $total",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(onClick = {}) {
            Text("Reintentar Quiz")
        }
    }
}