package com.practicum.playlistmaker.ui.media.models

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

data class PlaylistState(
    val data: Playlist?,
    val tracks: List<Track>?,
    val duration: Int?,
    val sharingString: String?,
    val isDeleted: Boolean?,
)