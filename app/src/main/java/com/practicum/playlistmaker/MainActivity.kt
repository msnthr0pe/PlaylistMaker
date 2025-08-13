package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemClickListener: View.OnClickListener = object : View.OnClickListener{override fun onClick(v: View?)
            {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }

        val searchBtn = findViewById<Button>(R.id.search_btn).setOnClickListener(itemClickListener)
        val mediaBtn = findViewById<Button>(R.id.media_btn).setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }
        val settingsBtn = findViewById<Button>(R.id.settings_btn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}