package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsBackButton = findViewById<View>(R.id.settings_back)

        settingsBackButton.setOnClickListener {
            val settingsBackIntent = Intent(this, MainActivity::class.java)
            startActivity(settingsBackIntent)
            finish()
        }

        val shareButton = findViewById<Button>(R.id.share_button)

        shareButton.setOnClickListener {
            val message = "https://practicum.yandex.ru/android-developer/"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.data = Uri.parse("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent,null)
            startActivity(chooserIntent)
        }

        val supportButton = findViewById<Button>(R.id.support_button)

        supportButton.setOnClickListener {
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("i.korastylev@yandex.ru"))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        val agreementButton = findViewById<Button>(R.id.agreement_button)

        agreementButton.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW,Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(agreementIntent)
        }
    }
}