package com.practicum.playlistmaker.data.search.history.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.data.search.network.StorageClient
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    context: Context,
    private val dataKey: String,
    private val type: Type,
) : StorageClient<T> {
    private val prefs: SharedPreferences = context.getSharedPreferences(HISTORY_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

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
    companion object {
        const val HISTORY_PREFS_NAME = "history_prefs"
    }

}