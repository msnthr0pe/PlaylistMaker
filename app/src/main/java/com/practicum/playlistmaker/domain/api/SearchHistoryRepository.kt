package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun saveToHistory(tracks: ArrayList<Track>)
    fun getHistory(): Resource<List<Track>>
}