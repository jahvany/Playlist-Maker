package com.practicum.playlistmaker.media.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class PlaylistState {
    object Loading : PlaylistState()
    object Empty : PlaylistState()
    data class Content(val tracks: List<Track>) : PlaylistState()
}