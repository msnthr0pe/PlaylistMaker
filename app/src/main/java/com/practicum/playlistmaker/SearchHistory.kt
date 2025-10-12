package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit

const val HISTORY_PREFS_NAME = "history_prefs"
const val HISTORY_PREFS_KEY = "history"

class SearchHistory {
    fun readHistory(prefs: SharedPreferences): ArrayList<Track>? {
        val historyJson = prefs
            .getString(HISTORY_PREFS_KEY, null) ?: return null
        val json = Gson().fromJson(historyJson, Array<Track>::class.java)

        return ArrayList(json.toList().reversed())
    }

    fun writeHistory(prefs: SharedPreferences, tracks: Array<Track>) {
        val json = Gson().toJson(tracks)
        prefs.edit {
            putString(HISTORY_PREFS_KEY, json)
        }
    }
}