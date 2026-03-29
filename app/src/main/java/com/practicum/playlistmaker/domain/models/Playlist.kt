package com.practicum.playlistmaker.domain.models

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val trackIds: List<Int>,
    val tracksAmount: Int,
)