package com.practicum.playlistmaker.media.data.db

import com.practicum.playlistmaker.media.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.media.data.converters.TrackPlaylistDbConverter
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.dao.TrackPlaylistDao
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackPlaylistDao: TrackPlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackPlaylistDbConverter
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao
            .getPlaylists()
            .map { entities ->
                entities.map { playlistDbConverter.map(it) }
            }
    }

    override suspend fun addToPlaylists(playlist: Playlist) {
        val entity = playlistDbConverter.map(playlist)
        playlistDao.insertPlaylists(listOf(entity))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlistDbConverter.map(playlist)
        playlistDao.updatePlaylist(entity)
    }

    override suspend fun updateTracks(track: Track, playlistId: Int) {
        val entityTrack = trackDbConverter.map(track, playlistId)
        trackPlaylistDao.insertTrackToPlaylist(listOf(entityTrack))
    }

    override fun getTrackPlaylist(playlistId: Int): Flow<List<Track>> {
        return playlistDao
            .getPlaylistById(playlistId)
            .filterNotNull()
            .flatMapLatest { playlistEntity ->
                val ids = playlistEntity.listOfTracks
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }

                trackPlaylistDao
                    .getTracks(playlistId)
                    .map { entities ->
                        entities
                            .filter { it.trackId in ids }
                            .map { trackDbConverter.map(it) }
                            .reversed()
                    }
            }
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistDao
            .getPlaylistById(id)
            .filterNotNull()
            .map { entity ->
                playlistDbConverter.map(entity)
            }
    }

    override suspend fun deletePlaylist(id: Int) {
        playlistDao.deletePlaylistById(id)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        trackPlaylistDao.deleteTrack(playlistId, trackId)
    }
}