package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchForTracks(expression: String): List<Track>?
}