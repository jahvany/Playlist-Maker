package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.media.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        val entity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTracks(listOf(entity))
    }

    override suspend fun removeFromFavoriteTracks(track: Track) {
        val entity = trackDbConverter.map(track)
        appDatabase.trackDao().deleteTrack(entity)
    }

    override suspend fun getFavoriteTrackIds(): List<Int> =
        appDatabase.trackDao().getTrackIds()

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

}

