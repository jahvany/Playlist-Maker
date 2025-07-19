package com.practicum.playlistmaker

class OnTrackClickListener(val tracksHistory: MutableList<Track>) {

    fun onTrackClick(track: Track) {
        if (track in tracksHistory) {
            tracksHistory.remove(track)
            tracksHistory.add(0, track)
        } else {
            tracksHistory.add(0, track)
            if (tracksHistory.size == 11) tracksHistory.removeAt(10)
        }
    }
}