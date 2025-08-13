package com.practicum.playlistmaker

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
                Toast.makeText(this@MainActivity, "Search button has been pressed", Toast.LENGTH_SHORT).show()
            }
        }

        val searchBtn = findViewById<Button>(R.id.search_btn).setOnClickListener(itemClickListener)
        val mediaBtn = findViewById<Button>(R.id.media_btn).setOnClickListener {
            Toast.makeText(this@MainActivity, "Media button has been pressed", Toast.LENGTH_SHORT).show()
        }
        val settingsBtn = findViewById<Button>(R.id.settings_btn).setOnClickListener {
            Toast.makeText(this@MainActivity, "Settings button has been pressed", Toast.LENGTH_SHORT).show()
        }
    }
}