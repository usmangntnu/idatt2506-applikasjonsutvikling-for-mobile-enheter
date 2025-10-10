package com.example.oving7.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oving7.viewmodel.FilmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: FilmViewModel, onBack: () -> Unit) {
    val colors = listOf("White", "Blue", "Green", "Gray")

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Innstillinger") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Tilbake")
                }
            }
        )
    }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Velg bakgrunnsfarge:")
            colors.forEach { color ->
                Button(
                    onClick = { viewModel.updateColor(color) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(color)
                }
            }
        }
    }
}
