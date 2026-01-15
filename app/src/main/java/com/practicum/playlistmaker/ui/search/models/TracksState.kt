package com.practicum.playlistmaker.ui.search.models

import com.practicum.playlistmaker.domain.models.Track

data class TracksState (
    val currentHistory: ArrayList<Track>,
    val displayedTracks: ArrayList<Track>,
    )