package com.practicum.playlistmaker.media.data.db

import com.practicum.playlistmaker.media.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.media.data.converters.TrackPlaylistDbConverter
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackPlaylistDbConverter
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao()
            .getPlaylists()
            .map { entities ->
                entities.map { playlistDbConverter.map(it) }
            }
    }

    override suspend fun addToPlaylists(playlist: Playlist) {
        val entity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylists(listOf(entity))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(entity)
    }

    override suspend fun updateTracks(track : Track) {
        val entityTrack = trackDbConverter.map(track)
        appDatabase.trackPlaylistDao().insertTrackToPlaylist(listOf(entityTrack))
    }

    override fun getTrackPlaylist(playlistId: Int): Flow<List<Track>> {
        return appDatabase.playlistDao()
            .getPlaylistById(playlistId)
            .filterNotNull()
            .flatMapLatest { playlistEntity ->
                val ids = playlistEntity.listOfTracks
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }

                appDatabase.trackPlaylistDao()
                    .getTracks()
                    .map { entities ->
                        entities
                            .filter { it.trackId in ids }
                            .map { trackDbConverter.map(it) }
                            .reversed()
                    }
            }
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return appDatabase.playlistDao()
            .getPlaylistById(id)
            .filterNotNull()
            .map { entity ->
                playlistDbConverter.map(entity)
            }
    }

    override suspend fun deletePlaylist(id: Int) {
        appDatabase.playlistDao().deletePlaylistById(id)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int) {
        appDatabase.trackPlaylistDao().deleteTrack(trackId)
    }
}