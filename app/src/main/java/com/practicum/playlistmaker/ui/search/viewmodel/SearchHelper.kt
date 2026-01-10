package com.practicum.playlistmaker.ui.search.viewmodel

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.TracksInteractor

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