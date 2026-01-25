package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    
    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun addToFavoriteTracks(track: Track)

    suspend fun removeFromFavoriteTracks(track: Track)

    suspend fun getFavoriteTrackIds(): List<Int>

}