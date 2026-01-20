package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PlaylistMakerApp : Application() {

    private val themeEditor = Creator.provideThemeEditor(this)

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