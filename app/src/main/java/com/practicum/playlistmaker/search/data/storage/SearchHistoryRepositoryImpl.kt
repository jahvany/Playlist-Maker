package com.practicum.playlistmaker.search.data.storage


import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.first

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>,
    private val appDatabase: AppDatabase
): SearchHistoryRepository {

    override fun saveHistory(history: List<Track>) {
        storage.storeData(ArrayList(history))
    }

    override suspend fun getHistory(): Resource<List<Track>> {
        val historyTracks = storage.getData() ?: arrayListOf()
        val favoriteIds = appDatabase
            .trackDao()
            .getTrackIds()
            .first()
            .toSet()
        val updatedHistory = historyTracks.map { track ->
            track.copy(
                isFavorite = favoriteIds.contains(track.trackId)
            )
        }
        return Resource.Success(updatedHistory)
    }
}