package com.example.oving7.data

import android.content.Context
import com.example.oving7.R
import java.io.*

class FileRepository(private val context: Context) {

    fun readFilmsFromRaw(): List<Film> {
        val inputStream = context.resources.openRawResource(R.raw.filmer)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readLines().map { line ->
            val parts = line.split(";")
            Film(
                tittel = parts[0],
                regissor = parts[1],
                skuespillere = parts[2]
            )
        }
    }

    fun writeFilmsToLocalFile(filmer: List<Film>) {
        val file = File(context.filesDir, "filmer_local.txt")
        file.writeText(filmer.joinToString("\n") {
            "${it.tittel};${it.regissor};${it.skuespillere}"
        })
    }
}
