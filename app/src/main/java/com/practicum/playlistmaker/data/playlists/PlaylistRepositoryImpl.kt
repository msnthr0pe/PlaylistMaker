package com.practicum.playlistmaker.data.playlists

import android.net.Uri
import com.practicum.playlistmaker.domain.playlists.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistImageLocalDataSource: PlaylistImageLocalDataSource,
): PlaylistRepository {
    override suspend fun savePlaylistCover(uri: Uri, fileName: String) {
        playlistImageLocalDataSource.savePlaylistCover(uri, fileName)
    }
}