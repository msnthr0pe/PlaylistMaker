package com.practicum.playlistmaker.data.playlists

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            trackIds = Gson().toJson(playlist.trackIds),
            tracksAmount = playlist.tracksAmount,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        val tracks: List<Int> = if (playlist.trackIds.isNotEmpty()) {
            Gson().fromJson(playlist.trackIds, object : TypeToken<List<Int>>() {}.type)
        } else {
            emptyList()
        }
        return Playlist(
            id = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            trackIds = tracks,
            tracksAmount = playlist.tracksAmount
        )
    }
}