package com.practicum.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.ui.media.models.PlaylistState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var data: Playlist? = null
    private var tracks: List<Track>? = null
    private var duration: Int? = null
    private var sharingString: String? = null
    private var isDeleted: Boolean? = null

    private val playlistState = MutableLiveData(
        PlaylistState(
            data,
            tracks,
            duration,
            sharingString,
            isDeleted,
        )
    )

    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

    fun getTracksByPlaylist(playlistId: Int) {
        viewModelScope.launch {
            tracks = playlistInteractor.getTracksInPlaylist(playlistId)
            duration = getTotalDuration()
            postState()
        }
    }

    fun getPlaylist(playlistId: Int) {
        viewModelScope.launch {
            data = playlistInteractor.getPlaylistById(playlistId)
            postState()
        }
    }

    private fun getTotalDuration(): Int {
        val totalDuration = tracks?.sumOf { it.trackTimeMillis }
        return convertToMinutes(totalDuration ?: 0)
    }

    fun removeTrackFromPlaylist(trackId: Long, playlistId: Int) {
        viewModelScope.launch {
            tracks = playlistInteractor.removeTrackFromPlaylistAndGet(trackId, playlistId)
            duration = getTotalDuration()
            postState()
        }
    }

    fun removePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.removePlaylist(playlistId)
            isDeleted = true
            postState()
        }
    }

    fun buildStringForSharing() {
        sharingString = buildString {
            appendLine("${data?.name}")
            if (data?.description?.isNotEmpty() == true) appendLine("${data?.description}")
            appendLine("${tracks?.size ?: 0} треков")
            tracks?.let { tracks ->
                tracks.forEachIndexed { index, track ->
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${convertToMinutes(track.trackTimeMillis)} минут)")
                }
            }
        }
        postState()
    }

    fun resetSharingState() {
        sharingString = null
        postState()
    }

    private fun convertToMinutes(millis: Long) =
        (millis / 60_000L).toInt()

    private fun postState() {
        playlistState.postValue(
            PlaylistState(
                data,
                tracks,
                duration,
                sharingString,
                isDeleted,
            )
        )
    }
}