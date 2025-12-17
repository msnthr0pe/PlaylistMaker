package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<Track>
) : Response()