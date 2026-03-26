package com.practicum.playlistmaker.domain.db.impl

import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.db.FavouritesRepository
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(
    private val repository: FavouritesRepository,
) : FavouritesInteractor {
    override suspend fun addFavourite(track: Track) {
        repository.addFavourite(track)
    }

    override suspend fun removeFavourite(track: Track) {
        repository.removeFavourite(track)
    }

    override fun getFavourites(): Flow<List<Track>> {
        return repository.getFavourites()
    }

}