package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerState.Prepared
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String?): ViewModel() {
    companion object {
        private const val TIME_CHEK_DELAY = 250L
    }
    private var mediaPlayer = MediaPlayer()
    private val state = MutableLiveData<PlayerState>()
    val stateLiveData: LiveData<PlayerState> = state

    private var timer = "00:00"
    private val handler = Handler(Looper.getMainLooper())

    private val setTimeRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                setTime()
                handler.postDelayed(this, TIME_CHEK_DELAY)
            }
        }
    }
    init {
        state.postValue(PlayerState.Default)
        preparePlayer()
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
                state.postValue(PlayerState.Prepared)
                resetTimer()
            }
        } catch (e: Exception) {
            state.postValue(PlayerState.Default)
            e.printStackTrace()
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        state.postValue(PlayerState.Playing(timer))
        handler.post(setTimeRunnable)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(setTimeRunnable)
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

    fun resetTimer() {
        handler.removeCallbacks(setTimeRunnable)
        state.postValue(state.value?.setTimer("00:00") ?: PlayerState.Default)
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        mediaPlayer.release()
    }
}