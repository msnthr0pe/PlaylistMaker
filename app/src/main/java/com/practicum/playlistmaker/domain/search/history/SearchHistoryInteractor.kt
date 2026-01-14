package com.practicum.playlistmaker.domain.search.history

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(tracks: ArrayList<Track>)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }
}