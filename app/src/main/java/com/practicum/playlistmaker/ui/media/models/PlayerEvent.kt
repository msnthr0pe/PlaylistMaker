package com.practicum.playlistmaker.ui.media.models

sealed class PlayerEvent(val playlistName: String) {
    class TrackAddSuccess(playlistName: String) : PlayerEvent(playlistName)
    class TrackAddDuplicate(playlistName: String) : PlayerEvent(playlistName)
}
