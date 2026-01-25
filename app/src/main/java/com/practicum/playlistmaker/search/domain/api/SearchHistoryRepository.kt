package com.practicum.playlistmaker.search.domain.api


import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface SearchHistoryRepository {
    fun saveHistory(history: List<Track>)
    suspend fun getHistory(): Resource<List<Track>>
}