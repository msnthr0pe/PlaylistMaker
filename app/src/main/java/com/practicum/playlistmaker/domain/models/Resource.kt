package com.practicum.playlistmaker.domain.models

sealed class Resource<T> {
    abstract val data: T?

    data class Success<T>(override val data: T) : Resource<T>()
    data class Error<T>(
        override val data: T? = null,
        val exception: Throwable? = null,
        val message: String? = null
    ) : Resource<T>()
}
