package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addToPlaylists(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)
}