package com.practicum.playlistmaker.domain.playlists

import android.net.Uri

interface PlaylistInteractor {
    suspend fun saveImage(uri: Uri, fileName: String)
}