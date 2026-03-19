package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchForTracks(expression: String): Flow<List<Track>?>
}