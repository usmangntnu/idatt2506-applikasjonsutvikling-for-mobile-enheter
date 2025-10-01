package com.example.oving6_client

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewId<TextView>(R.id.textView)
        Client(textView).start()
    }
}