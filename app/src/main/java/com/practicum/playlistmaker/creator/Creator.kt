package com.practicum.playlistmaker.creator

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.search.history.HistoryInteractor
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import com.practicum.playlistmaker.domain.search.history.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.history.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.domain.search.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideHistoryInteractor(prefs: SharedPreferences): HistoryInteractor {
        return HistoryInteractorImpl(HistoryRepositoryImpl(), prefs)
    }
}