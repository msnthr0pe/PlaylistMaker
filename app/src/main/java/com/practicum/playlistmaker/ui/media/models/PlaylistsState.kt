package com.practicum.playlistmaker.ui.media.models

import com.practicum.playlistmaker.domain.models.Playlist

data class PlaylistsState (
    val playlists: List<Playlist>,
    val playerEvent: PlayerEvent?,
)