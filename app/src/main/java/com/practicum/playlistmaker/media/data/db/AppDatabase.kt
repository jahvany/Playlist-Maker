package com.practicum.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.dao.TrackDao
import com.practicum.playlistmaker.media.data.db.dao.TrackPlaylistDao
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.media.data.db.entity.TrackPlaylistEntity

@Database(
    entities = [TrackEntity::class,
        PlaylistEntity::class,
        TrackPlaylistEntity::class ],
    version = 4,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun trackPlaylistDao(): TrackPlaylistDao
}