package com.example.oving7.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oving7.viewmodel.FilmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FilmViewModel, onOpenSettings: () -> Unit) {
    val filmer by viewModel.filmer
    val bgColor by viewModel.bgColor.collectAsState()

    val background = when (bgColor) {
        "Blue" -> Color(0xFFBBDEFB)
        "Green" -> Color(0xFFC8E6C9)
        "Gray" -> Color(0xFFE0E0E0)
        else -> Color.White
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filmer") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Innstillinger")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
        ) {
            LazyColumn {
                items(filmer) { film ->
                    Column(Modifier.padding(8.dp)) {
                        Text(film.tittel, fontWeight = FontWeight.Bold)
                        Text("Regissør: ${film.regissor}")
                        Text("Skuespillere: ${film.skuespillere}")
                    }
                    Divider()
                }
            }
        }
    }
}
