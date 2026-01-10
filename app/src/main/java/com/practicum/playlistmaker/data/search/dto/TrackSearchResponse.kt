package com.practicum.playlistmaker.data.search.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDTO>
) : Response()