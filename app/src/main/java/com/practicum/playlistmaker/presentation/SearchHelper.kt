package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track

object SearchHelper {
    fun getTracks(query: String, callback: (List<Track>?) -> Unit) {
        val tracksInteractor = Creator.provideTracksInteractor()
        tracksInteractor.searchForTracks(
            expression = query,
            object : TracksInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    callback(foundTracks)
                }
            }
        )
    }
}