package com.example.oving7.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Film::class], version = 1)
abstract class FilmDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}
