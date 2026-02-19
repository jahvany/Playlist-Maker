package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.updatePlaylist(playlist)
        }
    }
}