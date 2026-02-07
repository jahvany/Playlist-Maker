package com.practicum.playlistmaker.media.domain.models

data class Playlist(
    val id: Int = 0,
    val name: String,
    val cover: String,
    val description: String,
    var listOfTracks: String,
    var numbersOfTracks: Int
) {
}