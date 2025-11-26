package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    private var isClickAllowed = true

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    private var clickJob: Job? = null

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    var textForSave = ""

    fun loadHistory() {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (searchHistory.isNotEmpty()) {
                    _state.postValue(SearchState.History(searchHistory))
                } else {
                    _state.postValue(SearchState.Empty)
                }
            }
        })
    }

    fun saveTrack(track: Track) {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                searchHistoryInteractor.saveToHistory(track)
            }
        })
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchState.History(emptyList())
    }

    fun search(text: String) {
        if (text.isBlank()) {
            loadHistory()
        } else {
            _state.value = SearchState.Loading
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(text)
                    .collect { pair ->
                        if (pair.first == null) {
                            _state.postValue(SearchState.Error)
                        } else if (pair.first!!.isEmpty()) {
                            _state.postValue(SearchState.NothingFound)
                        } else {
                            _state.postValue(SearchState.Content(pair.first!!))
                        }
                    }
            }
        }
    }

    fun searchDebounce(text: String) {
        if (latestSearchText == text) return

        latestSearchText = text
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(text)
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}