package com.example.oving7.data

import androidx.room.*

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(filmer: List<Film>)

    @Query("SELECT * FROM Film")
    suspend fun getAll(): List<Film>

    @Query("SELECT * FROM Film WHERE regissor = :name")
    suspend fun getByRegissor(name: String): List<Film>

    @Query("SELECT * FROM Film WHERE tittel = :title")
    suspend fun getByTittel(title: String): List<Film>
}
