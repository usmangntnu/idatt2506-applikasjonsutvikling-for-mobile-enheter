package com.example.oving1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.oving1.ui.theme.Oving1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Oving1Theme {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    // State: om menyen er åpen eller ikke
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Meny-app") },
                actions = {
                    // Meny-knapp (⋮)
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Meny"
                        )
                    }

                    // Selve dropdown-menyen
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Usman") },
                            onClick = {
                                Log.w("MenuApp", "Fornavnet ditt ble valgt!") // warning
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ghafoorzai") },
                            onClick = {
                                Log.e("MenuApp", "Etternavnet ditt ble valgt!") // error
                                expanded = false
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Hovedinnhold. Bruker innerPadding for at innhold ikke skal gå under AppBar
        Text(
            text = "Velg et navn i menyen!",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    Oving1Theme {
        MyApp()
    }
}
