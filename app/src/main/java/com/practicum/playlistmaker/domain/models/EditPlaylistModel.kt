package com.practicum.playlistmaker.domain.models

import android.net.Uri

data class EditPlaylistModel(
    val id: Int,
    val coverUri: Uri?,
    val name: String,
    val description: String,
    val tracksAmount: Int = 0,
)