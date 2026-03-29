package com.practicum.playlistmaker.domain.playlists

import android.net.Uri
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
): PlaylistInteractor {
    override suspend fun createPlaylist(name: String, description: String, coverUri: Uri?) =
        playlistRepository.createPlaylist(name, description, coverUri)

    override suspend fun getPlaylists(): List<Playlist> =
        playlistRepository.getPlaylists()

}