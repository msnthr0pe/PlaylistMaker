package com.practicum.playlistmaker.data.playlists

import android.net.Uri
import androidx.room.withTransaction
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.data.db.PlaylistToTrackEntity
import com.practicum.playlistmaker.data.player.TrackDbConverter
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistImageLocalDataSource: PlaylistImageLocalDataSource,
    private val database: AppDatabase,
    private val playlistConverter: PlaylistDbConverter,
    private val trackConverter: TrackDbConverter,
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
        database.playlistDao().getPlaylists().map { playlistConverter.map(it) }

    private suspend fun updateTrackCountInPlaylist(playlistId: Int, tracksAmount: Int) {
        val playlistDao = database.playlistDao()
        val playlist = playlistDao.getPlaylistById(playlistId)
        playlist?.let {
            playlistDao.updatePlaylist(playlist.copy(tracksAmount = tracksAmount))
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Int): List<Playlist>? {
        return database.withTransaction {
            val playlistsTracksDao = database.playlistsTracksDao()
            playlistsTracksDao.insertTrackIntoPlaylist(
                PlaylistToTrackEntity(
                    playlistId = playlistId,
                    trackId = track.trackId,
                )
            )
            val trackIds = playlistsTracksDao.getTrackIdsInPlaylist(playlistId)

            database.tracksDao().insertTrack(trackConverter.map(track))

            updateTrackCountInPlaylist(playlistId, trackIds.size)

            val playlistDao = database.playlistDao()
            playlistDao.getPlaylists().map { playlistConverter.map(it) }
        }
    }

    override suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>? =
        database.playlistsTracksDao().getTrackIdsInPlaylist(playlistId)

    override suspend fun getTracksInPlaylist(playlistId: Int): List<Track> {
        return database.withTransaction {
            val trackIds = getTrackIdsInPlaylist(playlistId)
            trackIds?.let {
                database.tracksDao().getTracksByIds(it).map { trackConverter.map(it) }
            } ?: emptyList()
        }
    }

    override suspend fun removeTrackFromPlaylistAndGet(
        trackId: Long,
        playlistId: Int,
    ): List<Track> {
        return database.withTransaction {
            val playlistsTracksDao = database.playlistsTracksDao()
            playlistsTracksDao.removeTrackFromPlaylist(playlistId, trackId)
            val tracks = getTracksInPlaylist(playlistId)
            if (playlistsTracksDao.countPlaylistsForTrack(trackId) == 0) {
                database.tracksDao().removeTrack(trackId)
            }
             updateTrackCountInPlaylist(playlistId, tracks.size)
            tracks
        }
    }
}