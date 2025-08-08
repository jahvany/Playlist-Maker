package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class PlayerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val titleSearch = findViewById<MaterialToolbar>(R.id.titlePlayer)
        titleSearch.setNavigationOnClickListener { finish() }

        val cover = findViewById<ImageView>(R.id.cover)
        val radius = (8 * cover.context.resources.displayMetrics.density).roundToInt()

        val track = intent.getParcelableExtra<Track>("track")

        track?.let {
            Glide.with(cover)
                .load(it.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.bigplaceholder)
                .transform(RoundedCorners(radius))
                .into(cover)
            findViewById<TextView>(R.id.trackName).text = it.trackName
            findViewById<TextView>(R.id.artistName).text = it.artistName
            findViewById<TextView>(R.id.timeNumber).text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
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
    }
}
