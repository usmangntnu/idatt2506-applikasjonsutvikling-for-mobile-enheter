package com.example.oving7.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oving7.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FilmViewModel(
    private val filmDao: FilmDao,
    private val fileRepo: FileRepository,
    private val prefs: PreferencesRepository
) : ViewModel() {

    var filmer = androidx.compose.runtime.mutableStateOf<List<Film>>(emptyList())
        private set

    val bgColor = prefs.colorFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "White"
    )

    fun loadFilms(context: Context) {
        viewModelScope.launch {
            if (filmDao.getAll().isEmpty()) {
                val rawFilms = fileRepo.readFilmsFromRaw()
                filmDao.insertAll(rawFilms)
                fileRepo.writeFilmsToLocalFile(rawFilms)
            }
            filmer.value = filmDao.getAll()
        }
    }

    fun filterByRegissor(name: String) {
        viewModelScope.launch {
            filmer.value = filmDao.getByRegissor(name)
        }
    }

    fun filterByFilm(title: String) {
        viewModelScope.launch {
            filmer.value = filmDao.getByTittel(title)
        }
    }


    fun updateColor(color: String) {
        viewModelScope.launch { prefs.setColor(color) }
    }
}
