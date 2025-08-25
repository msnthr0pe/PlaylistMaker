package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchField = findViewById<EditText>(R.id.search_et)
        val clearBtn = findViewById<ImageView>(R.id.clear_text_btn)

        clearBtn.setOnClickListener {
            searchField.setText("")
        }

        clearBtn.visibility = View.GONE

        findViewById<ImageView>(R.id.back_search_btn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int,
            ) {}

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int,
            ) {
                if (searchField.text.isNotEmpty()) {
                    clearBtn.visibility = View.VISIBLE
                } else {
                    clearBtn.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        searchField.addTextChangedListener(textWatcher)
    }
}