package com.practicum.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.ui.media.models.PlaylistState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var tracks: List<Track>? = null

    private val playlistState = MutableLiveData(
        PlaylistState(
            tracks,
        )
    )
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

    fun getTracksByPlaylist(playlistId: Int) {
        viewModelScope.launch {
            tracks = playlistInteractor.getTracksInPlaylist(playlistId)
            postState()
        }
    }

    fun removeTrackFromPlaylist(trackId: Long, playlistId: Int) {
        viewModelScope.launch {
            tracks = playlistInteractor.removeTrackFromPlaylistAndGet(trackId, playlistId)
            postState()
        }
    }

    private fun postState() {
        playlistState.postValue(
            PlaylistState(
                tracks = tracks,
            )
        )
    }
}