package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class PlaylistMakerApp : Application() {

    private val themeEditor = Creator.provideThemeEditor(this)

    override fun onCreate() {
        super.onCreate()
        themeEditor.setDarkTheme()
    }

    fun setDarkTheme(setDark: Boolean) {
        themeEditor.setDarkTheme(setDark)
    }

    fun isDarkThemeEnabled(): Boolean = themeEditor.isDarkThemeEnabled
}