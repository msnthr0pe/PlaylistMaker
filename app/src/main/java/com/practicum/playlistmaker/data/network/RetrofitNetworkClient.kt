package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

object RetrofitNetworkClient : NetworkClient {
    private const val BASE_URL = "https://itunes.apple.com/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val searchMusicApi: SearchMusicApi = retrofit.create(SearchMusicApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            try {
                val resp = searchMusicApi.search(text = dto.text).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } catch (_: Exception){
                return Response()
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

}