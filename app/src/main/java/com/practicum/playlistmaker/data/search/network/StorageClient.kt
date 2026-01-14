package com.practicum.playlistmaker.data.search.network

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}