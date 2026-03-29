package com.practicum.playlistmaker.data.playlists

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            tracks = Gson().toJson(playlist.tracks),
            tracksAmount = playlist.tracksAmount,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        val tracks: List<Track> = if (playlist.tracks.isNotEmpty()) {
            Gson().fromJson(playlist.tracks, object : TypeToken<List<Track>>() {}.type)
        } else {
            emptyList()
        }
        return Playlist(
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            tracks = tracks,
            tracksAmount = playlist.tracksAmount
        )
    }
}