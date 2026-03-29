package com.practicum.playlistmaker.domain.playlists

import android.net.Uri
import com.practicum.playlistmaker.domain.models.Playlist

interface PlaylistRepository {
    suspend fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?,
    ): Long
    suspend fun getPlaylists(): List<Playlist>
    suspend fun addTrackToPlaylist(trackId: Long, playlistId: Int): List<Playlist>?
}