package com.practicum.playlistmaker.data.playlists

import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            tracksAmount = playlist.tracksAmount,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            coverUri = playlist.coverUri,
            tracksAmount = playlist.tracksAmount
        )
    }
}