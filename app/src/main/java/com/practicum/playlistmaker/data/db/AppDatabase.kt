package com.practicum.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [
    FavouritesEntity::class,
    PlaylistEntity::class,
    TrackEntity::class,
    PlaylistToTrackEntity::class
])
abstract class AppDatabase : RoomDatabase(){

    abstract fun favouritesDao(): FavouritesDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun tracksDao(): TracksDao
    abstract fun playlistsTracksDao(): PlaylistsTracksDao
}