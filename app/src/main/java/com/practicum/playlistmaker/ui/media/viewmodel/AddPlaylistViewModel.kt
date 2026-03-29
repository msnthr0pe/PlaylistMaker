package com.practicum.playlistmaker.ui.media.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {
    fun saveImageToFile(uri: Uri, filename: String) {
        viewModelScope.launch {
            playlistInteractor.saveImage(uri, filename)
        }
    }
}