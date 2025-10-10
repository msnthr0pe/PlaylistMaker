package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isDarkThemeEnabled = (applicationContext as PlaylistMakerApp).isDark

        val darkThemeSwitch = findViewById<SwitchCompat>(R.id.dark_theme_switch)

        if (isDarkThemeEnabled) {
                darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, isNightTheme ->
            (applicationContext as PlaylistMakerApp).setDarkTheme(isNightTheme)
        }
        findViewById<ImageView>(R.id.back_settings_btn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.share_app_btn).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, null))
        }

        findViewById<Button>(R.id.contact_support_btn).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            startActivity(intent)
        }

        findViewById<Button>(R.id.user_agreement_btn).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.legal_link).toUri()))
        }
    }
}