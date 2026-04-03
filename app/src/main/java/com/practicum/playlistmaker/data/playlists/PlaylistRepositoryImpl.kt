package com.practicum.playlistmaker.data.playlists

import android.net.Uri
import androidx.room.withTransaction
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.data.db.PlaylistToTrackEntity
import com.practicum.playlistmaker.data.player.TrackDbConverter
import com.practicum.playlistmaker.domain.models.EditPlaylistModel
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
        return database.withTransaction {
            val playlistDao = database.playlistDao()
            val lastInsertedId = playlistDao.insertPlaylist(
                PlaylistEntity(
                    playlistId = 0,
                    name = name,
                    description = description,
                    coverUri = "",
                    tracksAmount = 0,
                )
            )
            saveImage(lastInsertedId.toInt(), coverUri)

            lastInsertedId
        }
    }

    private suspend fun saveImage(id: Int, coverUri: Uri?) {
        val playlistDao = database.playlistDao()
        coverUri?.let {
            playlistImageLocalDataSource.savePlaylistCover(coverUri, id.toString())
                .onSuccess { file ->
                    val playlist = playlistDao.getPlaylistById(id)
                    playlist?.let {
                        val absolutePath = file.absolutePath
                        playlistDao.updatePlaylist(playlist.copy(coverUri = absolutePath))
                    }
                }
        }
    }

    override suspend fun getPlaylists(): List<Playlist> =
        database.playlistDao().getPlaylists().map { playlistConverter.map(it) }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        val playlistEntity = database.playlistDao().getPlaylistById(id)
        return playlistEntity?.let {
            playlistConverter.map(playlistEntity)
        }
    }

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

            if (trackIds.isNullOrEmpty()) {
                emptyList()
            } else {
                val tracks = database.tracksDao().getTracksByIds(trackIds)
                val trackMap = tracks.associateBy { it.trackId }

                trackIds.mapNotNull { trackMap[it] }
                    .map { trackConverter.map(it) }
            }
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

    override suspend fun removePlaylist(playlistId: Int) {
        database.withTransaction {
            val playlistsTracksDao = database.playlistsTracksDao()
            val tracksInPlaylist = playlistsTracksDao.getTrackIdsInPlaylist(playlistId)
            database.playlistDao().removePlaylist(playlistId)
            playlistsTracksDao.removePlaylist(playlistId)
            tracksInPlaylist.forEach { trackId ->
                if (playlistsTracksDao.countPlaylistsForTrack(trackId) == 0) {
                    database.tracksDao().removeTrack(trackId)
                }
            }
            tracksInPlaylist.size
        }
    }

    override suspend fun updatePlaylist(playlist: EditPlaylistModel): Playlist? {
        return database.withTransaction {
            val playlistDao = database.playlistDao()
            saveImage(playlist.id, playlist.coverUri)
            playlistDao.updatePlaylist(
                playlistId = playlist.id,
                name = playlist.name,
                description = playlist.description,
            )
            val playlist = playlistDao.getPlaylistById(playlist.id)
            playlist?.let {
                playlistConverter.map(it)
            }
        }
    }
}