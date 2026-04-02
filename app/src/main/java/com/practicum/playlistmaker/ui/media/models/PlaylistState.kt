package com.practicum.playlistmaker.ui.media.models

import com.practicum.playlistmaker.domain.models.Track

data class PlaylistState(
    val tracks: List<Track>?,
    val duration: Int?,
    val sharingString: String?,
    val isDeleted: Boolean?,
)