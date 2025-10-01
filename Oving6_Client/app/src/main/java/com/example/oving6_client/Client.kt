package com.example.oving6_client

import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class Client(
    private val textView: TextView,
    private val username: String,
    private val SERVER_IP: String = "10.0.2.2",
    private val SERVER_PORT: Int = 12345
) {
    private var socket: Socket? = null
    @Volatile private var writer: PrintWriter? = null
    @Volatile private var reader: BufferedReader? = null
    private val connected = AtomicBoolean(false)

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                appendToUI("Connecting to $SERVER_IP:$SERVER_PORT...\n")
                socket = Socket(SERVER_IP, SERVER_PORT)
                writer = PrintWriter(socket!!.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                connected.set(true)
                appendToUI("Connected to server: ${socket}\n")
                // Send username once (optional)
                writer?.println(username)

                // start reading incoming messages continuously
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var line: String? = null
                        while (connected.get() && reader!!.readLine().also { line = it } != null) {
                            appendToUI("$line\n")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        appendToUI("Read error: ${e.message}\n")
                    } finally {
                        disconnect()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                appendToUI("Connection error: ${e.message}\n")
            }
        }
    }

    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (writer != null && connected.get()) {
                    writer!!.println("$username: $message")
                    appendToUI("Me: $message\n")
                } else {
                    appendToUI("Not connected to server.\n")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                appendToUI("Send error: ${e.message}\n")
            }
        }
    }

    private fun appendToUI(msg: String) {
        MainScope().launch {
            textView.append(msg)
        }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                connected.set(false)
                writer?.close()
                reader?.close()
                socket?.close()
                appendToUI("Disconnected from server.\n")
            } catch (e: Exception) {
                e.printStackTrace()
                appendToUI("Disconnect error: ${e.message}\n")
            }
        }
    }
}
