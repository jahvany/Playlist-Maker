package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.player.domain.model.AddToPlaylistResult
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.PlayerController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val url: String?,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var controller: PlayerController? = null

    private var currentTrack: Track? = null

    private val _state = MutableLiveData(PlayerState())
    val stateLiveData: LiveData<PlayerState> = _state

    private val _list = MutableLiveData<List<Playlist>>()
    fun observeList(): LiveData<List<Playlist>> = _list

    private val _addResult = MutableLiveData<AddToPlaylistResult>()
    val addResult: LiveData<AddToPlaylistResult> = _addResult

    fun bindService(controller: PlayerController) {
        this.controller = controller
        viewModelScope.launch {
            controller.stateFlow.collect { playerState ->
                _state.postValue(playerState.copy(isFavorite = _state.value?.isFavorite ?: false))
            }
        }
    }

    fun setTrack(track: Track) {
        val currentState = controller?.getCurrentState()

        if (currentTrack?.trackId == track.trackId &&
            currentState?.status != PlayerStatus.DEFAULT) {
            return
        }

        viewModelScope.launch {
            val ids = favoriteInteractor.getFavoriteTrackIds().first()
            val isFav = track.trackId in ids

            val trackToSet = track.copy(isFavorite = isFav)
            currentTrack = trackToSet

            updateState { copy(isFavorite = isFav) }

            controller?.setTrack(trackToSet)

            if (currentState?.status == PlayerStatus.DEFAULT) {
                controller?.prepare(url)
            }
        }
    }

    fun playbackControl() {
        controller?.playbackControl()
    }

    fun appBackground() {
        if (_state.value?.status == PlayerStatus.PLAYING) {
            controller?.showNotification()
        }
    }

    fun appForeground() {
        controller?.hideNotification()
    }

    fun stopPlayer() {
        controller?.stopPlayer()
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        viewModelScope.launch {

            if (track.isFavorite) {
                favoriteInteractor.removeFromFavoriteTracks(track)
            } else {
                favoriteInteractor.addToFavoriteTracks(track)
            }

            currentTrack = track.copy(
                isFavorite = !track.isFavorite
            )

            updateState { copy(isFavorite = track.isFavorite) }
        }
    }

    fun getPlaylists() {

        viewModelScope.launch {

            playlistInteractor
                .getPlaylists()
                .collect {

                    _list.postValue(it)
                }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, trackId: Int) {
        val track = currentTrack ?: return
        viewModelScope.launch {

            val ids = playlist.listOfTracks
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (trackId.toString() in ids) {

                _addResult.postValue(
                    AddToPlaylistResult.AlreadyExists(playlist.name)
                )

                return@launch
            }

            val newIds = ids + trackId.toString()

            val updatedPlaylist = playlist.copy(
                listOfTracks = newIds.joinToString(","),
                numbersOfTracks = newIds.size
            )

            playlistInteractor.updatePlaylist(updatedPlaylist)

            playlistInteractor.updateTracks(track, playlist.id)

            _addResult.postValue(
                AddToPlaylistResult.Added(playlist.name)
            )
        }
    }

    private fun updateState(block: PlayerState.() -> PlayerState) {
        _state.value = _state.value?.block()
    }
}