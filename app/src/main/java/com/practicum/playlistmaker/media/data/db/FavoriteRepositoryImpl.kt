package com.practicum.playlistmaker.media.data.db

import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.data.db.dao.TrackDao
import com.practicum.playlistmaker.media.data.db.dao.TrackPlaylistDao
import com.practicum.playlistmaker.media.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao
            .getTracks()
            .map { entities ->
                entities.map { trackDbConverter.map(it) }
            }
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        val entity = trackDbConverter.map(track)
        trackDao.insertTracks(listOf(entity))
    }

    override suspend fun removeFromFavoriteTracks(track: Track) {
        val entity = trackDbConverter.map(track)
        trackDao.deleteTrack(entity)
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> =
        trackDao.getTrackIds()

}