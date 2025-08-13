package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsBtn = findViewById<ImageView>(R.id.back_settings_btn)
        settingsBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}