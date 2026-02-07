package com.practicum.playlistmaker.media.data.db

import com.practicum.playlistmaker.media.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
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

}