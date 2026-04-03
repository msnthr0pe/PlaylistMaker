package com.practicum.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.ui.media.models.PlayerEvent
import com.practicum.playlistmaker.ui.media.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private var playlists = listOf<Playlist>()
    private var playerEvent: PlayerEvent? = null
    private var playlistsState = MutableLiveData(
        PlaylistsState(
            playlists = playlists,
            playerEvent = playerEvent,
        )
    )

    fun observePlaylists(): LiveData<PlaylistsState> = playlistsState

    fun getPlaylists() {
        viewModelScope.launch {
            playlists = playlistInteractor.getPlaylists()
            playerEvent = null
            postState()
        }
    }

    fun addToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val trackIdsInPlaylist = playlistInteractor.getTrackIdsInPlaylist(playlist.id)
            if (trackIdsInPlaylist?.contains(track.trackId) == false) {
                playlists = playlistInteractor.addTrackToPlaylist(track, playlist.id) ?: playlists
                playerEvent = PlayerEvent.TrackAddSuccess(playlist.name)
            } else {
                playerEvent = PlayerEvent.TrackAddDuplicate(playlist.name)
            }
            postState()
        }
    }

    fun resetSnackbarState() {
        playerEvent = null
        postState()
    }

    private fun postState() {
        playlistsState.postValue(
            PlaylistsState(
                playlists,
                playerEvent,
            )
        )
    }
}