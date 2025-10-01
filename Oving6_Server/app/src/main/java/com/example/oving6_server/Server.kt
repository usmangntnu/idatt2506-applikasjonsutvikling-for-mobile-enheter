package com.example.oving6_server

import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket



class Server (
    private val textView: TextView,
    private val PORT: Int = 8080
)  {
    private var ui: String? = ""
        set(str) {
            MainScope().launch {
                textView.text = str
            }
            field = str
        }

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ui = "Initializing Server..."
                ServerSocket(PORT).use { serverSocket: ServerSocket ->
                    ui = "ServerSocket established, waiting for client..."
                    serverSocket.accept().use { clientSocket: Socket ->
                        ui = "A Client connected to:\n$clientSocket"
                        sendToClient(clientSocket, "Welcome Client!")
                        readFromClient(clientSocket)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ui = e.message
            }

        }
    }

    private fun readFromClient(socket: Socket) {
        val reder =
            BufferedReader(InputStreamReader(socket.getInputStream()))
        val message = reader.readLine()
        ui = "Client is saying:\n$message"
    }

}
