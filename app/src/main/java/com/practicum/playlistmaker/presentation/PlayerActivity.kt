package com.practicum.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class PlayerActivity : AppCompatActivity() {

    private lateinit var play: ImageButton
    private lateinit var playTime: TextView
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val setTimeRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                setTime()
                handler.postDelayed(this, TIME_CHEK_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getParcelableExtra<Track>("track")

        val titleSearch = findViewById<MaterialToolbar>(R.id.titlePlayer)
        titleSearch.setNavigationOnClickListener { finish() }

        val cover = findViewById<ImageView>(R.id.cover)
        val radius = (8 * cover.context.resources.displayMetrics.density).roundToInt()

        play = findViewById(R.id.playButton)
        play.animate()
            .alpha(0.5f)

        playTime = findViewById(R.id.playTime)

        track?.let {
            Glide.with(cover)
                .load(it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.bigplaceholder)
                .transform(RoundedCorners(radius))
                .into(cover)
            findViewById<TextView>(R.id.trackName).text = it.trackName
            findViewById<TextView>(R.id.artistName).text = it.artistName
            findViewById<TextView>(R.id.timeNumber).text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
            if (it.collectionName.isEmpty()) {
                findViewById<Group>(R.id.albumGroup).visibility = View.GONE
            } else {
                findViewById<Group>(R.id.albumGroup).visibility = View.VISIBLE
                findViewById<TextView>(R.id.albumName).text = it.collectionName
            }
            if (it.releaseDate.isEmpty()) {
                findViewById<Group>(R.id.yearGroup).visibility = View.GONE
            } else {
                findViewById<Group>(R.id.yearGroup).visibility = View.VISIBLE
                findViewById<TextView>(R.id.yearNumber).text = it.releaseDate.substring(0, 4)
            }
            findViewById<TextView>(R.id.countryName).text = it.country
            findViewById<TextView>(R.id.genreName).text = it.primaryGenreName
        }
        preparePlayer(track?.previewUrl)
        play.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }


    fun setTime() {
        playTime.setText(
            SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayer.currentPosition)
        )
    }

    private fun preparePlayer(url: String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.animate()
                .alpha(1f)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
            playTime.setText(R.string.zeroTime)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        handler.post(setTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(setTimeRunnable)
        play.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    companion object {
        private const val TIME_CHEK_DELAY = 250L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}
