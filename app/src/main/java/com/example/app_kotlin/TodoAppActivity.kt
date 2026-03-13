package com.example.app_kotlin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.ui.theme.AppkotlinTheme

class TodoAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppkotlinTheme {
                TodoApp(onBack = { finish() })
            }
        }
    }
}

data class TodoItem(val id: Int, val text: String, val done: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(onBack: () -> Unit) {
    val context = LocalContext.current
    val todos = remember {
        mutableStateListOf(
            TodoItem(id = 1, text = "Aprender Kotlin", done = true),
            TodoItem(id = 2, text = "Finalizar el curso")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo App", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nueva tarea") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Text("Listado", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(todos, key = { it.id }) { task ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth().clickable {
                        val index = todos.indexOf(task)
                        todos[index] = task.copy(done = !task.done)
                    }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = task.text,
                                modifier = Modifier.weight(1f),
                                textDecoration = if (task.done) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                            IconButton(onClick = { Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}