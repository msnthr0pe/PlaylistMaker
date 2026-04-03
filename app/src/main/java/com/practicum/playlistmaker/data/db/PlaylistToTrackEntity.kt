package com.practicum.playlistmaker.data.db

import androidx.room.Entity

@Entity(
    tableName = "playlist_track",
    primaryKeys = ["playlistId", "trackId"],
)
data class PlaylistToTrackEntity(
    val playlistId: Int,
    val trackId: Long,
    val createdAt: Long = System.currentTimeMillis()
)