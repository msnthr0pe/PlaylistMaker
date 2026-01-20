package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchForTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.searchForTracks(expression)
            withContext(Dispatchers.Main) {
                consumer.consume(result)
            }
        }
    }
}