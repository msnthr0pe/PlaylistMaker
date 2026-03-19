package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchForTracks(expression: String): Flow<List<Track>?> {
        return repository.searchForTracks(expression)
    }
}