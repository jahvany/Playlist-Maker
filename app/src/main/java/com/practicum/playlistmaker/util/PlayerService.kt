package com.practicum.playlistmaker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerService : Service(), PlayerController {

    private val binder = PlayerBinder()

    private val mediaPlayer = MediaPlayer()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var timerJob: Job? = null

    private var currentTrack: Track? = null

    private val _stateFlow = MutableStateFlow(PlayerState())
    override val stateFlow: StateFlow<PlayerState> get() = _stateFlow

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    private fun updateState(block: PlayerState.() -> PlayerState) {
        _stateFlow.value = _stateFlow.value.block()
    }

    inner class PlayerBinder : Binder() {
        fun getController(): PlayerController = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun observeState(): StateFlow<PlayerState> = stateFlow

    override fun setTrack(track: Track) {
        currentTrack = track
    }

    override fun prepare(url: String?) {

        if (url.isNullOrEmpty()) return

        if (_stateFlow.value.status != PlayerStatus.DEFAULT) return

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
                        timer = getString(R.string.zeroTime)
                    )
                }

                hideNotification()
            }

        } catch (e: Exception) {
            updateState { copy(status = PlayerStatus.DEFAULT) }
        }
    }

    override fun playbackControl() {
        when (_stateFlow.value.status) {
            PlayerStatus.PLAYING -> pause()
            PlayerStatus.PREPARED,
            PlayerStatus.PAUSED -> start()
            else -> Unit
        }
    }

    private fun start() {

        showNotification()

        mediaPlayer.start()
        updateState { copy(status = PlayerStatus.PLAYING) }
        startTimer()
    }

    private fun pause() {

        mediaPlayer.pause()

        timerJob?.cancel()

        updateState { copy(status = PlayerStatus.PAUSED) }
    }

    private fun startTimer() {

        timerJob?.cancel()

        timerJob = serviceScope.launch {

            while (mediaPlayer.isPlaying) {

                delay(TIME_CHECK_DELAY)

                val time = dateFormat.format(mediaPlayer.currentPosition)

                updateState {
                    copy(
                        status = PlayerStatus.PLAYING,
                        timer = time
                    )
                }
            }
        }
    }

    override fun stopPlayer() {
        timerJob?.cancel()
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.reset()

        updateState { copy(status = PlayerStatus.DEFAULT, timer = getString(R.string.zeroTime)) }

        hideNotification()
        stopSelf()
    }

    override fun getCurrentState(): PlayerState {
        return _stateFlow.value
    }

    override fun showNotification() {
        val track = currentTrack ?: return
        createChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("${track.artistName} - ${track.trackName}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun hideNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Playlist Maker",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {

        timerJob?.cancel()

        mediaPlayer.release()

        serviceScope.cancel()

        super.onDestroy()
    }
    companion object {
        const val CHANNEL_ID = "player_channel"
        const val NOTIFICATION_ID = 1
        private const val TIME_CHECK_DELAY = 300L
    }
}
