package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addToPlaylists(playlist: Playlist) {
        return playlistRepository.addToPlaylists(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun updateTracks(track : Track) {
        return playlistRepository.updateTracks(track)
    }

    override fun getTrackPlaylist(playlistId: Int): Flow<List<Track>> {
        return playlistRepository.getTrackPlaylist(playlistId)
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun deletePlaylist(id: Int) {
        playlistRepository.deletePlaylist(id)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int) {
        playlistRepository.deleteTrackFromPlaylist(trackId)
    }

}