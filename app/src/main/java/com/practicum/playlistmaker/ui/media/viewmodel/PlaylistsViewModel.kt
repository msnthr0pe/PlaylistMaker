package com.practicum.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    fun observePlaylistsState(): LiveData<List<Playlist>> = _playlists

    fun getPlaylists() {
        viewModelScope.launch {
            _playlists.postValue(playlistInteractor.getPlaylists())
        }
    }
}