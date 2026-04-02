package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.db.FavouritesDbConverter
import com.practicum.playlistmaker.data.db.FavouritesRepositoryImpl
import com.practicum.playlistmaker.data.player.TrackDbConverter
import com.practicum.playlistmaker.data.playlists.PlaylistDbConverter
import com.practicum.playlistmaker.data.playlists.PlaylistRepositoryImpl
import com.practicum.playlistmaker.data.search.history.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.db.FavouritesRepository
import com.practicum.playlistmaker.domain.playlists.PlaylistRepository
import com.practicum.playlistmaker.domain.search.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory {
        FavouritesDbConverter()
    }

    factory {
        PlaylistDbConverter()
    }

    factory {
        TrackDbConverter()
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get())
    }
}