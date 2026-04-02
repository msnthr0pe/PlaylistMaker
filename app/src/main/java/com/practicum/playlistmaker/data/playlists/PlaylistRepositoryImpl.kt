package com.practicum.playlistmaker.data.playlists

import android.net.Uri
import androidx.room.withTransaction
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.data.db.PlaylistToTrackEntity
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
                tracksAmount = 0,
            )
        )
    }

    override suspend fun getPlaylists(): List<Playlist> =
        database.playlistDao().getPlaylists().map { converter.map(it) }

    override suspend fun addTrackToPlaylist(trackId: Long, playlistId: Int): List<Playlist>? {
        return database.withTransaction {
            val playlistsTracksDao = database.playlistsTracksDao()
            playlistsTracksDao.insertTrackIntoPlaylist(
                PlaylistToTrackEntity(
                    playlistId = playlistId,
                    trackId = trackId,
                )
            )
            val trackIds = playlistsTracksDao.getTrackIdsInPlaylist(playlistId)

            val playlistDao = database.playlistDao()
            val playlist = playlistDao.getPlaylistById(playlistId)
            playlist?.let {
                playlistDao.updatePlaylist(playlist.copy(tracksAmount = trackIds.size))
            }

            playlistDao.getPlaylists().map { converter.map(it) }
        }
    }

    override suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>? =
        database.playlistsTracksDao().getTrackIdsInPlaylist(playlistId)
}