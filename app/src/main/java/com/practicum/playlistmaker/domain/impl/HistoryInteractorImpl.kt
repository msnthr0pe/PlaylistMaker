package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class HistoryInteractorImpl(
    private val repository: HistoryRepository,
    private val prefs: SharedPreferences,
    ) : HistoryInteractor {
    override fun getHistory(): ArrayList<Track>? {
        return repository.readHistory(prefs)
    }

    override fun putHistory(tracks: ArrayList<Track>) {
        repository.writeHistory(prefs, tracks)
    }
}