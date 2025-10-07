package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    private var isClickAllowed = true

    var textForSearch = ""
    val searchRunnable = Runnable { search(textForSearch) }

    private val handler = Handler(Looper.getMainLooper())

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

            tracksInteractor.searchTracks(text, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    if (foundTracks.isEmpty()) {
                        _state.postValue(SearchState.NothingFound)
                    } else {
                        _state.postValue(SearchState.Content(foundTracks))
                    }
                }

                override fun onFailure(error: Throwable) {
                    _state.postValue(SearchState.Error)
                }
            })
        }
    }

    fun searchDebounce(text: String) {
        textForSearch = text
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }
}