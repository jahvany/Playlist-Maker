package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val cover: String,
    val descriptor: String,
    var listOfTracks: String,
    var numbersOfTracks: Int
)