package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsBtn = findViewById<ImageView>(R.id.back_settings_btn)

        val darkThemeSwitch = findViewById<SwitchCompat>(R.id.dark_theme_switch)


        darkThemeSwitch.setOnCheckedChangeListener { _, isNightTheme ->
            Log.d("themeInfo", "$isNightTheme")
            if (isNightTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        settingsBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}