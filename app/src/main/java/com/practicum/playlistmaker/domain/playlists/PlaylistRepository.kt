package com.practicum.playlistmaker.domain.playlists

import android.net.Uri

interface PlaylistRepository {

    suspend fun savePlaylistCover(uri: Uri, fileName: String)
}