package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import java.lang.Exception

class RetrofitNetworkClient(
    val searchMusicApi: SearchMusicApi
) : NetworkClient {

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