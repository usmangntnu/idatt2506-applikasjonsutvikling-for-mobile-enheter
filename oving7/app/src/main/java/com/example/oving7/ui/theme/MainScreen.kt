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

    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Alle filmer") }
    var filterText by remember { mutableStateOf("") }

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
                .padding(16.dp)
        ) {

            // Filtermeny
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedFilter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Velg visning") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Alle filmer") },
                        onClick = {
                            selectedFilter = "Alle filmer"
                            expanded = false
                            viewModel.loadFilms(LocalContext.current)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Filmer etter regissør") },
                        onClick = {
                            selectedFilter = "Filmer etter regissør"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Skuespillere for film") },
                        onClick = {
                            selectedFilter = "Skuespillere for film"
                            expanded = false
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Søkefelt – vises bare ved filtrering
            if (selectedFilter != "Alle filmer") {
                OutlinedTextField(
                    value = filterText,
                    onValueChange = { filterText = it },
                    label = {
                        Text(
                            if (selectedFilter == "Filmer etter regissør")
                                "Søk etter regissør"
                            else
                                "Søk etter filmtittel"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            val context = LocalContext.current
                            if (selectedFilter == "Filmer etter regissør") {
                                viewModel.filterByRegissor(filterText)
                            } else {
                                viewModel.filterByFilm(filterText)
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Søk")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Liste over filmer
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
