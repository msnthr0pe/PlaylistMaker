package com.practicum.playlistmaker.domain.models

data class Playlist(
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: List<Track>,
    val tracksAmount: Int,
)