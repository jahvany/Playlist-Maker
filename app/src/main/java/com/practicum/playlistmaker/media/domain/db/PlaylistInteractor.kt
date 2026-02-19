package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {


    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addToPlaylists(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun updateTracks(track : Track)

    fun getTrackPlaylist(playlistId: Int): Flow<List<Track>>

    fun getPlaylistById(id: Int): Flow<Playlist>

    suspend fun deletePlaylist(id: Int)

}