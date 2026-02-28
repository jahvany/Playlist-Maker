package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.models.PlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor : SharingInteractor
) : ViewModel() {

    var currentPlaylist: Playlist? = null

    private val _state = MutableLiveData<PlaylistState>()

    fun observeState(): LiveData<PlaylistState> = _state

    private val _playlistState = MutableLiveData<Playlist>()
    val playlistState: LiveData<Playlist> = _playlistState

    private fun renderPlaylist(playlist: Playlist) {
        _playlistState.postValue(playlist)
    }

    private fun renderState(state: PlaylistState) {
        _state.postValue(state)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(tracks))
        }
    }

    fun observePlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .collect { playlist ->
                    currentPlaylist = playlist
                    renderPlaylist(playlist)
                    loadTracks()
                }
        }
    }

    fun loadTracks() {
        val playlist = currentPlaylist ?: return
        renderState(PlaylistState.Loading)
        viewModelScope.launch {
            playlistInteractor.getTrackPlaylist(playlist.id)
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }
    fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        val playlist = currentPlaylist ?: return
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
            val ids = playlist.listOfTracks
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() && it != trackId.toString() }

            val updatedPlaylist = playlist.copy(
                listOfTracks = ids.joinToString(","),
                numbersOfTracks = ids.size
            )
            playlistInteractor.updatePlaylist(updatedPlaylist)
            currentPlaylist = updatedPlaylist
            loadTracks()
        }
    }

    fun deletePlaylist() {
        val playlist = currentPlaylist ?: return
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist.id)
        }
    }

    fun sharePlaylist() {
        val playlist = currentPlaylist ?: return
        val tracks = (_state.value as? PlaylistState.Content)?.tracks ?: return

        if (tracks.isEmpty()) return

        val text = buildString {
            appendLine(playlist.name)
            appendLine(playlist.description)
            appendLine("${tracks.size} треков")
            tracks.forEachIndexed { index, track ->
                val time = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(track.trackTimeMillis)
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${time})")
            }
        }
        sharingInteractor.shareApp(text)
    }

}
