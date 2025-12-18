package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): ArrayList<Track>?
    fun putHistory(tracks: ArrayList<Track>)
} 