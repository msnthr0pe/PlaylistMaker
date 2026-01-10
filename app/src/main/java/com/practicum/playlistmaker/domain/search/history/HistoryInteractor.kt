package com.practicum.playlistmaker.domain.search.history

import com.practicum.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): ArrayList<Track>?
    fun putHistory(tracks: ArrayList<Track>)
} 