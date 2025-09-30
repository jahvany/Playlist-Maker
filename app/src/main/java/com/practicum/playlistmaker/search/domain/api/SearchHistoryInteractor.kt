package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(track: Track)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }
}