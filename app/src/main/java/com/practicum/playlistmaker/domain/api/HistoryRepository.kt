package com.practicum.playlistmaker.domain.api

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun readHistory(prefs: SharedPreferences): ArrayList<Track>?
    fun writeHistory(prefs: SharedPreferences, tracks: ArrayList<Track>)
}