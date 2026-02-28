package com.practicum.playlistmaker.media.domain.models


sealed class MediaPlaylistState {
    object Loading : MediaPlaylistState()
    object Empty : MediaPlaylistState()
    data class Content(val playlists: List<Playlist>) : MediaPlaylistState()
}