package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchMusicApi {

    @GET("search")
    fun search(
        @Query("entity") entity: String = "song",
        @Query("term") text: String
    ): Call<TrackSearchResponse>
}