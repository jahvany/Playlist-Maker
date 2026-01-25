package com.practicum.playlistmaker.media.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FavoriteState {
    object Loading : FavoriteState()
    object Empty : FavoriteState()
    data class Content(val tracks: List<Track>) : FavoriteState()
}