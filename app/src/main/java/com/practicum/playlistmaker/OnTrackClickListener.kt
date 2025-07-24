package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent

class OnTrackClickListener(private val context: Context, private val tracksHistory: MutableList<Track>) {

    fun onTrackClick(track: Track) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("track", track)
        context.startActivity(intent)
        if (track in tracksHistory) {
            tracksHistory.remove(track)
            tracksHistory.add(0, track)
        } else {
            tracksHistory.add(0, track)
            if (tracksHistory.size == 11) tracksHistory.removeAt(10)
        }
    }
}