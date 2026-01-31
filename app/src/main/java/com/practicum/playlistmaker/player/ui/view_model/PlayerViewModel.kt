package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String?,
    private val mediaPlayer: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    companion object {
        private const val TIME_CHECK_DELAY = 300L
    }

    private lateinit var currentTrack: Track

    private val _state = MutableLiveData(PlayerState())
    val stateLiveData: LiveData<PlayerState> = _state

    private var timerJob: Job? = null

    fun preparePlayer() {
        if (url.isNullOrEmpty()) return

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                updateState { copy(status = PlayerStatus.PREPARED) }
            }

            mediaPlayer.setOnCompletionListener {
                timerJob?.cancel()
                updateState {
                    copy(
                        status = PlayerStatus.PREPARED,
                        timer = "00:00"
                    )
                }
            }
        } catch (e: Exception) {
            updateState { copy(status = PlayerStatus.DEFAULT) }
        }
    }

    fun playbackControl() {
        when (_state.value?.status) {
            PlayerStatus.PLAYING -> pausePlayer()
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> startPlayer()
            else -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        updateState { copy(status = PlayerStatus.PLAYING) }
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        updateState { copy(status = PlayerStatus.PAUSED) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(TIME_CHECK_DELAY)
                val time = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)

                updateState { copy(timer = time) }
            }
        }
    }

    fun setTrack(track: Track) {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTrackIds().collect { ids ->
                val isFav = track.trackId in ids
                currentTrack = track.copy(isFavorite = isFav)
                updateState { copy(isFavorite = isFav) }
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (currentTrack.isFavorite) {
                favoriteInteractor.removeFromFavoriteTracks(currentTrack)
            } else {
                favoriteInteractor.addToFavoriteTracks(currentTrack)
            }

            currentTrack = currentTrack.copy(isFavorite = !currentTrack.isFavorite)
            updateState { copy(isFavorite = currentTrack.isFavorite) }
        }
    }


    private fun updateState(block: PlayerState.() -> PlayerState) {
        _state.value = _state.value?.block()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.reset()
    }
}