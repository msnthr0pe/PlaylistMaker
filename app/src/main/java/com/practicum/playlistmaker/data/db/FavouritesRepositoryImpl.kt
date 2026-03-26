package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.domain.db.FavouritesRepository
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val database: AppDatabase,
    private val converter: FavouritesDbConverter,
) : FavouritesRepository {
    override suspend fun addFavourite(track: Track) {
        database.favouritesDao().insertFavourite(converter.map(track))
    }

    override suspend fun removeFavourite(track: Track) {
        database.favouritesDao().removeFavourite(converter.map(track))
    }

    override fun getFavourites(): Flow<List<Track>> = flow {
        val favourites = database.favouritesDao().getFavourites()
        emit(favourites.map { converter.map(it) })
    }

}