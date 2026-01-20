package com.practicum.playlistmaker.data.theme

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

const val THEME_PREFS_NAME = "theme_prefs"
const val THEME_PREFS_KEY = "theme"

class ThemeEditorClient(private val context: Context) {
    var isDarkThemeEnabled = false
    private val themePrefs by lazy { context.getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE) }

    fun setDarkTheme(setDark: Boolean = themePrefs.getBoolean(THEME_PREFS_KEY, false)) {
        if (setDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        isDarkThemeEnabled = setDark
        themePrefs.edit {
            putBoolean(THEME_PREFS_KEY, isDarkThemeEnabled)
        }
    }
}