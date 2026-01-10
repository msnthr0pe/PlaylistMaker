package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchForTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}