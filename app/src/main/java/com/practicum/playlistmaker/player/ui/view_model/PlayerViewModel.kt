package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerState.Prepared
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String?, private val mediaPlayer: MediaPlayer): ViewModel() {
    companion object {
        private const val TIME_CHEK_DELAY = 300L
    }
    private val state = MutableLiveData<PlayerState>()
    val stateLiveData: LiveData<PlayerState> = state

    private var timer = "00:00"

    private var timerJob: Job? = null

    init {
        state.postValue(PlayerState.Default)
    }
    fun setTime() {
        timer = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
        state.postValue(state.value?.setTimer(timer) ?: PlayerState.Default)
    }

    fun preparePlayer() {
        if (url.isNullOrEmpty()) {
            state.postValue(PlayerState.Default)
            return
        }
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                state.postValue(PlayerState.Prepared)
            }
            mediaPlayer.setOnCompletionListener {
                timerJob?.cancel()
                timer = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(0)
                state.postValue(PlayerState.Prepared)
            }
        } catch (e: Exception) {
            state.postValue(PlayerState.Default)
            e.printStackTrace()
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        state.postValue(PlayerState.Playing(timer))
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        state.postValue(PlayerState.Paused(timer))
    }

    fun playbackControl() {
        when (state.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> null
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(TIME_CHEK_DELAY)
                setTime()
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        mediaPlayer.reset()
    }
}