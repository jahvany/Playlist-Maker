package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}