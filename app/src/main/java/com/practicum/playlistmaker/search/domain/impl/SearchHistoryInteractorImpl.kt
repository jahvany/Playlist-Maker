package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data?: emptyList())
    }

    override fun saveToHistory(track: Track) {
        val currentHistory = repository.getHistory().data?.toMutableList() ?: mutableListOf()

        currentHistory.remove(track)

        currentHistory.add(0, track)

        if (currentHistory.size > 10) {
            currentHistory.subList(10, currentHistory.size).clear()
        }

        repository.saveHistory(currentHistory)
    }

    override fun clearHistory() {
        repository.saveHistory(emptyList())
    }
}