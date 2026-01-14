package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun saveToHistory(m: Track)
    fun getHistory(): Resource<List<Track>>
}