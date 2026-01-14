package com.practicum.playlistmaker.data.search.history.impl

import com.practicum.playlistmaker.data.search.network.StorageClient
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
): SearchHistoryRepository {

    override fun saveToHistory(tracks: ArrayList<Track>) {
        storage.storeData(tracks)
    }

    override fun getHistory(): Resource.Success<ArrayList<Track>> {
        val tracks = storage.getData() ?: arrayListOf()
        return Resource.Success(tracks.apply { reverse() })
    }
}