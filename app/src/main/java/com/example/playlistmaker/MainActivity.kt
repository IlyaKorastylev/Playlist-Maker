package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<View>(R.id.search_button)

        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(imageClickListener)

        val libraryButton = findViewById<View>(R.id.library_button)

        libraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Медиатека!", Toast.LENGTH_SHORT).show()
        }

        val settingsButton = findViewById<View>(R.id.settings_button)

        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Настройки!", Toast.LENGTH_SHORT).show()
        }
    }
}