package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Delete
    suspend fun removePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT tracksAmount FROM playlists WHERE playlistId = :playlistId")
    suspend fun getTrackCount(playlistId: Int): Int

    @Query("SELECT trackIds FROM playlists WHERE playlistId = :playlistId")
    suspend fun getTracksJSON(playlistId: Int): String

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?

    @Transaction
    suspend fun addTrackToPlaylistAndGetAll(
        trackId: Long,
        playlistId: Int
    ): List<PlaylistEntity>? {

        val playlist = getPlaylistById(playlistId) ?: return null

        val gson = Gson()
        val currentTracks = if (playlist.trackIds.isNotEmpty()) {
            gson.fromJson<List<Long>>(
                playlist.trackIds,
                object : TypeToken<List<Long>>() {}.type
            ).toMutableList()
        } else {
            mutableListOf()
        }

        if (!currentTracks.contains(trackId)) {
            currentTracks.add(trackId)

            val updatedPlaylist = playlist.copy(
                trackIds = gson.toJson(currentTracks),
                tracksAmount = currentTracks.size
            )
            updatePlaylist(updatedPlaylist)
        }

        return getPlaylists()
    }

    @Transaction
    suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>? {
        val playlist = getPlaylistById(playlistId) ?: return null

        val gson = Gson()
        return if (playlist.trackIds.isNotEmpty()) {
            gson.fromJson<List<Long>>(
                playlist.trackIds,
                object : TypeToken<List<Long>>() {}.type
            ).toMutableList()
        } else {
            mutableListOf()
        }
    }
}