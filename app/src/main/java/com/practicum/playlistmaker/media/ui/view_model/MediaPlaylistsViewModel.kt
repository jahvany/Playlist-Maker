package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.models.MediaPlaylistState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<MediaPlaylistState>()

    fun observeState(): LiveData<MediaPlaylistState> = _state

    private var isClickAllowed = true

    private var clickJob: Job? = null

    private fun renderState(state: MediaPlaylistState) {
        _state.postValue(state)
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(MediaPlaylistState.Empty)
        } else {
            renderState(MediaPlaylistState.Content(playlists))
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            viewModelScope.launch {
                interactor
                    .getPlaylists()
                    .collect { playlists ->
                        processResult(playlists)
                    }
            }
        }
    }
    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickJob = viewModelScope.launch {
                delay(MediaPlaylistsViewModel.Companion.CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}