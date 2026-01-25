package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.PlaylistMakerApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isDarkThemeEnabled = (requireActivity().applicationContext as PlaylistMakerApp).isDarkThemeEnabled()

        val darkThemeSwitch = binding.darkThemeSwitch

        if (isDarkThemeEnabled) {
            darkThemeSwitch.isChecked = true
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, isNightTheme ->
            (requireActivity().applicationContext as PlaylistMakerApp).setDarkTheme(isNightTheme)
        }
        binding.apply {
            backSettingsBtn.setOnClickListener {
                //finish()
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

    companion object {
        const val TAG = "SettingsFragment"
    }
}