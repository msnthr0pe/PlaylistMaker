package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.ui.player.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.ui.search.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel { parameters ->
        PlayerViewModel(
            songUrl = parameters.get<String>()
        )
    }
}