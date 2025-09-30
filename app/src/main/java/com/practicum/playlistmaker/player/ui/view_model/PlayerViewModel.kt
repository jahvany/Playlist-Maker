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
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String?): ViewModel() {
    companion object {
        private const val TIME_CHEK_DELAY = 250L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        fun getFactory(url: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(url)
            }
        }
    }
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private var timer ="00:00"
    private val timerLiveData = MutableLiveData(timer)
    fun observeTime(): LiveData<String> = timerLiveData

    private val stateLiveData = MutableLiveData(playerState)
    fun observeState(): LiveData<Int> = stateLiveData

    private val setTimeRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                setTime()
                handler.postDelayed(this, TIME_CHEK_DELAY)
            }
        }
    }
    init {
        preparePlayer()
    }
    fun setTime() {
        timer = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
        timerLiveData.postValue(timer)
    }

    fun preparePlayer() {
        if (url.isNullOrEmpty()) {
            playerState = STATE_DEFAULT
            stateLiveData.postValue(playerState)
            return
        }
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
                stateLiveData.postValue(playerState)
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                stateLiveData.postValue(playerState)
                resetTimer()
            }
        } catch (e: Exception) {
            playerState = STATE_DEFAULT
            stateLiveData.postValue(playerState)
            e.printStackTrace()
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        stateLiveData.postValue(playerState)
        handler.post(setTimeRunnable)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(setTimeRunnable)
        playerState = STATE_PAUSED
        stateLiveData.postValue(playerState)
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun resetTimer() {
        handler.removeCallbacks(setTimeRunnable)
        timer ="00:00"
        timerLiveData.postValue(timer)
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        mediaPlayer.release()
    }
}