package com.example.oving7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.oving7.data.*
import com.example.oving7.ui.AppNav
import com.example.oving7.viewmodel.FilmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            FilmDatabase::class.java,
            "film_db"
        ).build()

        val fileRepo = FileRepository(this)
        val prefs = PreferencesRepository(this)
        val viewModel = FilmViewModel(db.filmDao(), fileRepo, prefs)

        viewModel.loadFilms(this)

        setContent {
            AppNav(viewModel)
        }
    }
}
