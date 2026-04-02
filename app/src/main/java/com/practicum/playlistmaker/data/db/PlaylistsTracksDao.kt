package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistsTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackIntoPlaylist(playlistTrack: PlaylistToTrackEntity)

    @Delete
    suspend fun deleteTrackFromPlaylist(playlistTrack: PlaylistToTrackEntity)

    @Query("SELECT trackId FROM playlist_track WHERE playlistId = :playlistId ORDER BY createdAt DESC")
    suspend fun getTrackIdsInPlaylist(playlistId: Int): List<Long>
}