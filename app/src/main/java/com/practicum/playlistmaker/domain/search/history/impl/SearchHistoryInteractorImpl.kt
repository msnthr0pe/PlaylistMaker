package com.practicum.playlistmaker.domain.search.history.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.history.SearchHistoryInteractor

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data)
    }

    override fun saveToHistory(tracks: ArrayList<Track>) {
        if (tracks.isNotEmpty() && tracks.count{it == tracks.last()} > 1) {
            val lastElement = tracks.last()
            tracks.remove(lastElement)
        }
        if (tracks.size == 11) {
            tracks.removeAt(0)
        }
        repository.saveToHistory(tracks)
    }
}