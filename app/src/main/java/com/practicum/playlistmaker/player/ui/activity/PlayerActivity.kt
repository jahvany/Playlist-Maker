package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }


    private lateinit var play: ImageButton
    private lateinit var playTime: TextView

    private lateinit var viewModel: PlayerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getParcelableExtra<Track>("track")

        val titleSearch = findViewById<MaterialToolbar>(R.id.titlePlayer)
        titleSearch.setNavigationOnClickListener { finish() }

        val cover = findViewById<ImageView>(R.id.cover)
        val radius = (8 * cover.context.resources.displayMetrics.density).roundToInt()

        play = findViewById(R.id.playButton)

        playTime = findViewById(R.id.playTime)

        val url = track?.previewUrl
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(url)
        )[PlayerViewModel::class.java]

        viewModel.observeState().observe(this) {
            updateButton(it)
        }

        viewModel.observeTime().observe(this) {
            updateTimer(it)
        }

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

        viewModel.preparePlayer()

        play.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    fun updateButton(state: Int) {
        when (state) {
            STATE_DEFAULT -> play.animate().alpha(0.5f)
            STATE_PREPARED -> {
                play.animate().alpha(1f)
                play.setImageResource(R.drawable.play)
            }
            STATE_PLAYING -> play.setImageResource(R.drawable.pause)
            STATE_PAUSED -> play.setImageResource(R.drawable.play)
        }
    }

    fun updateTimer(time: String) {
        playTime.text = time
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

}