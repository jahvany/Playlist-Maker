package com.practicum.playlistmaker.media.ui.view_model

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist
import java.io.File
import kotlin.math.roundToInt

class PlaylistViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

    private val playlistName: TextView = parentView.findViewById(R.id.playlistName)

    private val tracksNumber: TextView = parentView.findViewById(R.id.tracksNumber)
    private val cover = parentView.findViewById<ImageView>(R.id.cover)
    private val radius = (8 * cover.context.resources.displayMetrics.density).roundToInt()


    fun bind(playlist : Playlist) {
        playlistName.text = playlist.name
        tracksNumber.text = itemView.context.resources.getQuantityString(
            R.plurals.track_count,
            playlist.numbersOfTracks,
            playlist.numbersOfTracks
        )

        val context = itemView.context
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, playlist.cover)

        Glide.with(cover)
            .load(file)
            .placeholder(R.drawable.placeholder_playlist_holder)
            .error(R.drawable.placeholder_playlist_holder)
            .transform(RoundedCorners(radius))
            .into(cover)
    }
}