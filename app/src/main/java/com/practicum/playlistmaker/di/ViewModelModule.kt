package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.ui.media.viewmodel.FavouritesViewModel
import com.practicum.playlistmaker.ui.media.viewmodel.MediaViewModel
import com.practicum.playlistmaker.ui.media.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.ui.player.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.ui.search.viewmodel.SearchViewModel
import com.practicum.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel()
    }

    viewModel {
        SettingsViewModel()
    }

    viewModel {
        MediaViewModel()
    }

    viewModel {
        FavouritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}