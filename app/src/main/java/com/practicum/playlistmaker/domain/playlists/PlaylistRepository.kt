package com.practicum.playlistmaker.domain.playlists

import android.net.Uri
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

interface PlaylistRepository {
    suspend fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?,
    ): Long
    suspend fun getPlaylists(): List<Playlist>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Int): List<Playlist>?
    suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>?

    suspend fun getTracksInPlaylist(playlistId: Int): List<Track>

    suspend fun removeTrackFromPlaylistAndGet(trackId: Long, playlistId: Int): List<Track>
}