package com.practicum.playlistmaker

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)