package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.media.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository
) : FavoriteInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.getFavoriteTracks()
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        return favoriteRepository.addToFavoriteTracks(track)
    }

    override suspend fun removeFromFavoriteTracks(track: Track) {
        return favoriteRepository.removeFromFavoriteTracks(track)
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return favoriteRepository.getFavoriteTrackIds()
    }
}