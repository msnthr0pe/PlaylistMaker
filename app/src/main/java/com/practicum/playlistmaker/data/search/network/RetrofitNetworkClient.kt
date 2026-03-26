package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class RetrofitNetworkClient(
    val searchMusicApi: SearchMusicApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return withContext(Dispatchers.IO) {
            if (dto is TrackSearchRequest) {
                try {
                    val resp = searchMusicApi.search(text = dto.text)
                    resp.apply { resultCode = 200 }
                } catch (_: Exception) {
                    Response()
                }
            } else {
                Response().apply { resultCode = 400 }
            }
        }
    }

}