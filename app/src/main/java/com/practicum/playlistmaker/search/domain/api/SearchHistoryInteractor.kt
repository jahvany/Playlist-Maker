package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    suspend fun getHistory(): List<Track>
    suspend fun saveToHistory(track: Track)
    suspend fun clearHistory()
}