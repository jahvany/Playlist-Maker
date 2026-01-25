package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao()
            .getTracks()
            .map { entities ->
                entities.map { trackDbConverter.map(it) }
            }
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        val entity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTracks(listOf(entity))
    }

    override suspend fun removeFromFavoriteTracks(track: Track) {
        val entity = trackDbConverter.map(track)
        appDatabase.trackDao().deleteTrack(entity)
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> =
        appDatabase.trackDao().getTrackIds()

}

