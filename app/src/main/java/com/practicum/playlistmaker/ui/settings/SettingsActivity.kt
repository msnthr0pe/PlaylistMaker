package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.PlaylistMakerApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isDarkThemeEnabled = (applicationContext as PlaylistMakerApp).isDark

        val darkThemeSwitch = binding.darkThemeSwitch

        if (isDarkThemeEnabled) {
                darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, isNightTheme ->
            (applicationContext as PlaylistMakerApp).setDarkTheme(isNightTheme)
        }
        binding.apply {
            backSettingsBtn.setOnClickListener {
                finish()
            }

            shareAppBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, null))
            }

            contactSupportBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = "mailto:".toUri()
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
                startActivity(intent)
            }

            userAgreementBtn.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.legal_link).toUri()))
            }
        }
    }
}