package com.practicum.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: String,
    val tracksAmount: Int,
    val createdAt: Long = System.currentTimeMillis(),
)