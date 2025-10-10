package com.example.oving7.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

class PreferencesRepository(private val context: Context) {
    companion object {
        val BG_COLOR = stringPreferencesKey("bg_color")
    }

    val colorFlow: Flow<String> = context.dataStore.data.map {
        it[BG_COLOR] ?: "White"
    }

    suspend fun setColor(color: String) {
        context.dataStore.edit { it[BG_COLOR] = color }
    }
}
