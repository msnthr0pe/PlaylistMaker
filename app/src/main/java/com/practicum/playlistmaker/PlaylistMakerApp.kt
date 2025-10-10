package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

const val THEME_PREFS_NAME = "theme_prefs"
const val THEME_PREFS_KEY = "theme"
class PlaylistMakerApp : Application() {
    var isDark = false
    private lateinit var themePrefs : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        themePrefs = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        isDark = themePrefs.getBoolean(THEME_PREFS_KEY, false)
        setDarkTheme(isDark)
    }

    fun setDarkTheme(setDark: Boolean) {
        if (setDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        isDark = setDark
        themePrefs.edit {
            putBoolean(THEME_PREFS_KEY, isDark)
        }
    }
}