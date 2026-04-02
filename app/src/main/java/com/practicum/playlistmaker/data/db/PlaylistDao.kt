package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun removePlaylist(playlistId: Int)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT tracksAmount FROM playlists WHERE playlistId = :playlistId")
    suspend fun getTrackCount(playlistId: Int): Int

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("""
        UPDATE playlists 
        SET name = :name, 
            description = :description
        WHERE playlistId = :playlistId
    """)
    suspend fun updatePlaylist(
        playlistId: Int,
        name: String,
        description: String,
    ): Int

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?
}