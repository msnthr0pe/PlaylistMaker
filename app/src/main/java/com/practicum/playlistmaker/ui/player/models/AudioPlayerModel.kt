package com.practicum.playlistmaker.ui.player.models


data class AudioPlayerModel(
    val progressTime: String,
    val favouriteButtonState: FavouriteState,
    val isPlaying: Boolean,
)