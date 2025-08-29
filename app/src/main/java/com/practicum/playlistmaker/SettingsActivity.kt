package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val darkThemeSwitch = findViewById<SwitchCompat>(R.id.dark_theme_switch)

        //Из-за двух вызовов активити опять происходят баги с темой
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, isNightTheme ->
            Log.d("themeInfo", "$isNightTheme")
            if (isNightTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
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