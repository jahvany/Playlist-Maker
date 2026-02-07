package com.practicum.playlistmaker.media.domain.models


sealed class PlaylistState {
    object Loading : PlaylistState()
    object Empty : PlaylistState()
    data class Content(val playlists: List<Playlist>) : PlaylistState()
}