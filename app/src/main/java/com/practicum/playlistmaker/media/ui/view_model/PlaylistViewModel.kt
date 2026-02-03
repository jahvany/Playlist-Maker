package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.models.PlaylistState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>()

    fun observeState(): LiveData<PlaylistState> = _state

    private fun renderState(state: PlaylistState) {
        _state.postValue(state)
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(playlists))
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
}