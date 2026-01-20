package com.practicum.playlistmaker.data.search.history.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.data.search.network.StorageClient
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val dataKey: String,
    private val type: Type,
    private val prefs: SharedPreferences,
    private val gson: Gson,
) : StorageClient<T> {

    override fun storeData(data: T) {
        prefs.edit { putString(dataKey, gson.toJson(data, type)) }
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        return if (dataJson == null) {
            null
        } else {
            gson.fromJson(dataJson, type)
        }
    }
}