package com.practicum.playlistmaker.ui.media.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor

class AddPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {
    suspend fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?,
    ) {
        playlistInteractor.createPlaylist(name, description, coverUri)
    }
}