package com.example.oving7.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Film(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tittel: String,
    val regissor: String,
    val skuespillere: String
)
