package com.practicum.playlistmaker.domain.db

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {
    suspend fun addFavourite(track: Track)

    suspend fun removeFavourite(track: Track)

    fun getFavourites(): Flow<List<Track>>
}