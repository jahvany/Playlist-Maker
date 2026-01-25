package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.iTunesRequest
import com.practicum.playlistmaker.search.data.dto.iTunesResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(iTunesRequest(expression))
        when (response.resultCode) {
            200 -> {
                val favoriteIds = appDatabase.trackDao().getTrackIds().first().toSet()
                with(response as iTunesResponse) {
                    val data = response.results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            favoriteIds.contains(it.trackId)
                        )
                    }
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Ошибка сервера", emptyList()))
            }
        }
    }
}