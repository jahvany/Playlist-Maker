package com.practicum.playlistmaker.search.domain.models

sealed class SearchState {
    object Loading : SearchState()
    object Error : SearchState()
    object NothingFound : SearchState()
    object Empty : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}