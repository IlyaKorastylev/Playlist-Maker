package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val searchBackButton = findViewById<View>(R.id.search_back)

        searchBackButton.setOnClickListener {
            val searchBackIntent = Intent(this, MainActivity::class.java)
            startActivity(searchBackIntent)
        }
    }
}