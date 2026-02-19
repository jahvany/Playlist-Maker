package com.practicum.playlistmaker.media.ui.view_model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistAdapter(
    private var playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_media, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            onItemClick(playlists[holder.adapterPosition])
        }
    }



    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}