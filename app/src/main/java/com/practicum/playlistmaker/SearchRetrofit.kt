package com.practicum.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchRetrofit {
    private const val BASE_URL = "https://itunes.apple.com/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val searchMusicApi: SearchMusicApi = retrofit.create(SearchMusicApi::class.java)

}