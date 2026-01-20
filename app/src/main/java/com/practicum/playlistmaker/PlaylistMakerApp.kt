package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.data.theme.ThemeEditorClient
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PlaylistMakerApp : Application() {

    private val themeEditor: ThemeEditorClient by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        themeEditor.setDarkTheme()
    }

    fun setDarkTheme(setDark: Boolean) {
        themeEditor.setDarkTheme(setDark)
    }

    fun isDarkThemeEnabled(): Boolean = themeEditor.isDarkThemeEnabled
}