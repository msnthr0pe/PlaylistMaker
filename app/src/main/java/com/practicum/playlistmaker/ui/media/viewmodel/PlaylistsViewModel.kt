package com.practicum.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistsViewModel(): ViewModel() {

    fun getStubPlaylists(): List<Playlist> {
        return List(10) { i ->
            Playlist(
                name = "Playlist ${i + 1}",
                description = "Описание плейлиста ${i + 1}",
                coverUri = "https://example.com/cover${i + 1}.jpg",
                tracks = emptyList(),
                tracksAmount = (10..100).random()
            )
        }
    }
}