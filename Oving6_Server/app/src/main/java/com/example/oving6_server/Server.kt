package com.example.oving6_server

import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Server(
    private val textView: TextView,
    private val PORT: Int = 12345
) {
    // trådsikker liste over klient-writere
    private val clients = CopyOnWriteArrayList<PrintWriter>()
    private val clientNames = ConcurrentHashMap<PrintWriter, String>()
    private val clientIdCounter = AtomicInteger(1)

    private fun appendToUI(str: String) {
        MainScope().launch {
            // append for å beholde historikk
            textView.append(str)
        }
    }

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                appendToUI("Starting server on port $PORT...\n")
                ServerSocket(PORT).use { serverSocket ->
                    appendToUI("ServerSocket established; waiting for clients...\n")
                    while (true) {
                        val clientSocket = serverSocket.accept()
                        // håndter hver klient i egen coroutine
                        CoroutineScope(Dispatchers.IO).launch {
                            handleClient(clientSocket)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                appendToUI("Server error: ${e.message}\n")
            }
        }
    }

    private fun handleClient(socket: Socket) {
        val writer = PrintWriter(socket.getOutputStream(), true)
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val id = "Client-${clientIdCounter.getAndIncrement()}"
        clients.add(writer)
        clientNames[writer] = id

        appendToUI("$id connected: ${socket.remoteSocketAddress}\n")
        writer.println("SERVER: Welcome $id!")

        // fortell andre at en ny klient har koblet seg på
        broadcast("SERVER: $id joined the chat\n", sender = null)

        try {
            var message: String?
            while (reader.readLine().also { message = it } != null) {
                val msg = message!!.trim()
                if (msg.isNotEmpty()) {
                    appendToUI("$id: $msg\n")
                    broadcast("$id: $msg\n", sender = writer)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // fjern klient ved disconnect
            clients.remove(writer)
            clientNames.remove(writer)
            appendToUI("$id disconnected\n")
            broadcast("SERVER: $id left the chat\n", sender = null)
            try { socket.close() } catch (ignored: Exception) {}
        }
    }

    // sender til alle klienter; hvis sender != null, unngå å echo tilbake til sender
    private fun broadcast(message: String, sender: PrintWriter?) {
        for (client in clients) {
            try {
                if (sender != null && client == sender) continue
                client.println(message)
            } catch (e: Exception) {
                // ignorér feil per klient – fjernes ved disconnect i handleClient
                e.printStackTrace()
            }
        }
    }
}
