package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Delete
    suspend fun removePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT tracksAmount FROM playlists WHERE playlistId = :playlistId")
    suspend fun getSongCount(playlistId: Int): Int

    @Query("SELECT tracks FROM playlists WHERE playlistId = :playlistId")
    suspend fun getTracksJSON(playlistId: Int): String
}