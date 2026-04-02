package com.practicum.playlistmaker.domain.playlists

import android.net.Uri
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
): PlaylistInteractor {
    override suspend fun createPlaylist(name: String, description: String, coverUri: Uri?) =
        playlistRepository.createPlaylist(name, description, coverUri)

    override suspend fun getPlaylists(): List<Playlist> =
        playlistRepository.getPlaylists()

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Int): List<Playlist>? =
        playlistRepository.addTrackToPlaylist(track, playlistId)

    override suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>? =
        playlistRepository.getTrackIdsInPlaylist(playlistId)

    override suspend fun getTracksInPlaylist(playlistId: Int): List<Track> =
        playlistRepository.getTracksInPlaylist(playlistId)

    override suspend fun removeTrackFromPlaylistAndGet(
        trackId: Long,
        playlistId: Int,
    ): List<Track> =
        playlistRepository.removeTrackFromPlaylistAndGet(trackId, playlistId)
}