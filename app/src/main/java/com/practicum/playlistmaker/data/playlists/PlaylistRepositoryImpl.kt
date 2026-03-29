package com.practicum.playlistmaker.data.playlists

import android.net.Uri
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.playlists.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistImageLocalDataSource: PlaylistImageLocalDataSource,
    private val database: AppDatabase,
    private val converter: PlaylistDbConverter,
): PlaylistRepository {
    override suspend fun createPlaylist(name: String, description: String, coverUri: Uri?): Long {
        var path = ""
        coverUri?.let {
            playlistImageLocalDataSource.savePlaylistCover(coverUri, name)
                .onSuccess { file ->
                    path = file.absolutePath
                }
        }

        return database.playlistDao().insertPlaylist(
            PlaylistEntity(
                playlistId = 0,
                name = name,
                description = description,
                coverUri = path,
                tracks = "",
                tracksAmount = 0,
            )
        )
    }

    override suspend fun getPlaylists(): List<Playlist> =
        database.playlistDao().getPlaylists().map { converter.map(it) }
}