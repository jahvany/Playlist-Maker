package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlin.math.roundToInt

class TrackViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

    private val trackName: TextView = parentView.findViewById(R.id.trackName)
    private val artistName: TextView = parentView.findViewById(R.id.artistName)
    private val trackTime: TextView = parentView.findViewById(R.id.trackTime)
    private val cover = parentView.findViewById<ImageView>(R.id.cover)
    private val radius = (2 * cover.context.resources.displayMetrics.density).roundToInt()

    fun bind(track : Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        val imageUrl = track.artworkUrl100
        Glide.with(cover)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(cover)
    }
}