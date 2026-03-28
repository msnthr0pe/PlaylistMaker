package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.domain.models.Track

class FavouritesDbConverter {

    fun map(track: Track): FavouritesEntity {
        return FavouritesEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
        )
    }

    fun map(favourite: FavouritesEntity): Track {
        return Track(
            trackId = favourite.trackId,
            trackName = favourite.trackName,
            artistName = favourite.artistName,
            trackTimeMillis = favourite.trackTimeMillis,
            artworkUrl100 = favourite.artworkUrl100,
            collectionName = favourite.collectionName,
            releaseDate = favourite.releaseDate,
            primaryGenreName = favourite.primaryGenreName,
            country = favourite.country,
            previewUrl = favourite.previewUrl,
        )
    }
}
