package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE trackId = :trackId")
    suspend fun removeTrack(trackId: Long)

    @Query("SELECT * FROM tracks ORDER BY createdAt DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favourites ORDER BY createdAt DESC")
    suspend fun getTracksIds(): List<Long>

    @Query("SELECT * FROM tracks WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<TrackEntity>
}