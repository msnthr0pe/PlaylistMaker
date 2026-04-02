package com.practicum.playlistmaker.ui.media.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.EditPlaylistModel
import com.practicum.playlistmaker.domain.playlists.PlaylistInteractor

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val uriLiveData: MutableLiveData<String?> = MutableLiveData(null)
    fun observeUri(): LiveData<String?> = uriLiveData
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
        val uri = playlistInteractor.updatePlaylist(EditPlaylistModel(
            id = id,
            coverUri = coverUri,
            name = name,
            description = description,
        ))
        uriLiveData.postValue(uri)
    }

    fun resetUri() {
        uriLiveData.postValue(null)
    }
}