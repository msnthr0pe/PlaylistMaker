package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val darkThemeSwitch = findViewById<SwitchCompat>(R.id.dark_theme_switch)

        //Не могу догадаться, как сделать рабочий переключатель темы. Проблема в том,
        //что при переключении на другую активность и обратно переключатель становится
        //в неправильное положение относительно включённой тёмной темы, а при попытке
        //программно поставить его в это положение возникает бесконечный цикл с бесконечной
        //сменой тем
        darkThemeSwitch.setOnCheckedChangeListener { _, isNightTheme ->
            Log.d("themeInfo", "$isNightTheme")
            if (isNightTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        findViewById<ImageView>(R.id.back_settings_btn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.share_app_btn).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, null))
        }

        findViewById<LinearLayout>(R.id.contact_support_btn).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.user_agreement_btn).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.legal_link).toUri()))
        }
    }
}