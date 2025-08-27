package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.domain.models.Track

class OnTrackClickListener(
    private val context: Context,
    private val tracksHistory: MutableList<Track>,
    private val debounce: () -> Boolean
) {

    fun onTrackClick(track: Track) {
        if (!debounce()) return
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("track", track)
        context.startActivity(intent)
        if (track in tracksHistory) tracksHistory.remove(track)
        tracksHistory.add(0, track)
        if (tracksHistory.size == 11) tracksHistory.removeAt(10)
    }
}