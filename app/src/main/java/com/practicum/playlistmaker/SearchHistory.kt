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

    fun writeHistory(prefs: SharedPreferences, tracks: ArrayList<Track>) {
        if (tracks.isNotEmpty() && tracks.count{it == tracks.last()} > 1) {
            val lastElement = tracks.last()
            tracks.remove(lastElement)
        }
        if (tracks.size == 11) {
            tracks.removeAt(0)
        }
        val json = Gson().toJson(tracks.toTypedArray())
        prefs.edit {
            putString(HISTORY_PREFS_KEY, json)
        }
    }
}