package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistsTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackIntoPlaylist(playlistTrack: PlaylistToTrackEntity)

    @Query("DELETE FROM playlist_track WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Long): Int

    @Query("SELECT trackId FROM playlist_track WHERE playlistId = :playlistId ORDER BY createdAt DESC")
    suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>
}