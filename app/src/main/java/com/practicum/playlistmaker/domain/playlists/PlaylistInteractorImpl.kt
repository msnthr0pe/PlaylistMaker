package com.practicum.playlistmaker.domain.playlists

import android.net.Uri

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
): PlaylistInteractor {
    override suspend fun saveImage(uri: Uri, fileName: String) {
        playlistRepository.savePlaylistCover(uri, fileName)
    }
}