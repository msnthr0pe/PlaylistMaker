package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.search.history.impl.PrefsStorageClient
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.network.SearchMusicApi
import com.practicum.playlistmaker.data.search.network.StorageClient
import com.practicum.playlistmaker.domain.models.Track
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

const val BASE_URL = "https://itunes.apple.com/"
const val HISTORY_PREFS_NAME = "history_prefs"
const val HISTORY_PREFS_KEY = "history"

val dataModule = module {

    single<SearchMusicApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchMusicApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(HISTORY_PREFS_NAME, Context.MODE_PRIVATE)
    }

    single<Type> {
        object : TypeToken<ArrayList<Track>>() {}.type
    }

    factory { Gson() }

    factory<StorageClient<ArrayList<Track>>> {
        PrefsStorageClient(get(), HISTORY_PREFS_KEY, get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient
    }

}