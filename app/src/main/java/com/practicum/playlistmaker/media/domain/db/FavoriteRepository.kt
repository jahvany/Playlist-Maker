package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface  FavoriteRepository {

    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun addToFavoriteTracks(track: Track)

    suspend fun removeFromFavoriteTracks(track: Track)

    fun getFavoriteTrackIds(): Flow<List<Int>>

}