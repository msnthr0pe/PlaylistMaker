package com.practicum.playlistmaker.ui.search.models

import com.practicum.playlistmaker.domain.models.Track

data class SearchState (
    val displayedTracks: ArrayList<Track>,
    val placeholdersState: PlaceholdersState,
    val isHistoryEnabled: Boolean,
)