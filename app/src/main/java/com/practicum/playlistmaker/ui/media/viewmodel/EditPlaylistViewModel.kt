package com.practicum.playlistmaker.ui.media.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.EditPlaylistModel
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val playlistLiveData: MutableLiveData<Playlist?> = MutableLiveData(null)
    fun observeState(): LiveData<Playlist?> = playlistLiveData
    suspend fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?,
    ) {
        playlistInteractor.createPlaylist(name, description, coverUri)
    }

    suspend fun updatePlaylist(
        id: Int,
        name: String,
        description: String,
        coverUri: Uri?,
    ) {
        val playlist = playlistInteractor.updatePlaylist(EditPlaylistModel(
            id = id,
            coverUri = coverUri,
            name = name,
            description = description,
        ))
        playlistLiveData.postValue(playlist)
    }

    fun getPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlistLiveData.postValue(playlist)
        }
    }

    fun resetUri() {
        playlistLiveData.postValue(null)
    }
}