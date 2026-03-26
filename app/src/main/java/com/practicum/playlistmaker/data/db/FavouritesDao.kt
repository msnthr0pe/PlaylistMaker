package com.practicum.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouritesEntity)

    @Delete
    suspend fun removeFavourite(favourite: FavouritesEntity)

    @Query("SELECT * FROM favourites")
    suspend fun getFavourites(): List<FavouritesEntity>

    @Query("SELECT trackId FROM favourites")
    suspend fun getFavouriteIds(): List<Long>
}