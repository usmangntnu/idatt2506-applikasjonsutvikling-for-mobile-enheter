package com.example.oving6_client

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity() {
    private var client: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContentView(R.layout.activity_main)

        val chatText = findViewById<TextView>(R.id.textView)
        val usernameEdit = findViewById<EditText>(R.id.usernameEditText)
        val connectBtn = findViewById<Button>(R.id.connectButton)
        val messageEdit = findViewById<EditText>(R.id.messageEditText)
        val sendBtn = findViewById<Button>(R.id.sendButton)

        connectBtn.setOnClickListener {
            val username = usernameEdit.text.toString().ifBlank { "Anon" }
            client = Client(chatText, username, SERVER_IP = "10.0.2.2", SERVER_PORT = 12345)
            client?.start()
            connectBtn.isEnabled = false
            usernameEdit.isEnabled = false
            sendBtn.isEnabled = true
        }

        sendBtn.setOnClickListener {
            val msg = messageEdit.text.toString()
            if (msg.isNotBlank()) {
                client?.sendMessage(msg)
                messageEdit.text.clear()
            }
        }
    }
}
