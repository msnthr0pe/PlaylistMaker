package com.practicum.playlistmaker.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favouritesInteractor: FavouritesInteractor,
): ViewModel() {
    private val _favourites = MutableLiveData(emptyList<Track>())
    fun observeFavourites(): LiveData<List<Track>> = _favourites

    fun initLoadFavourites() {
        viewModelScope.launch {
            favouritesInteractor.getFavourites().collect {
                _favourites.postValue(it)
            }
        }
    }
}