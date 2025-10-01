package com.example.oving6_client

import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client(
    private val textView: TextView,
    private val SERVER_IP: String = "10.0.2.2",
    private val SERVER_PORT: Int = 12345,
) {
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            ui = "Kobler til tjener..."
            try {
                Socket(SERVER_IP, SERVER_PORT).use { socket: Socket ->
                    ui = "Koblet til tjener:\n$socket"
                    delay(5000)
                    readFromServer(socket)
                    delay(5000)
                    sendToServer(socket, "Heisann Tjener! Hyggelig aa hilse paa deg")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ui = e.message
            }
        }
    }

    private fun readFromServer(socket: Socket) {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val message = reader.readLine()
        ui = "Melding fra tjeneren:\n$message"
    }

    private fun sendToServer(socket: Socket, message: String) {
        val writer = PrintWriter(socket.getOutputStream(), true)
        writer.println(message)
        ui = "Sendte foelgende til tjeneren: \n\"$message\""
    }

    private var ui: String? = ""
        set(str) {
            MainScope().launch { textView.text = str }
            field = str
        }
}
