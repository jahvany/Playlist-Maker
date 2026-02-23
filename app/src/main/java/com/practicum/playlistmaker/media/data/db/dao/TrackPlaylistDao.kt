package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.TrackPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(tracks: List<TrackPlaylistEntity>)

    @Query("SELECT * FROM trackplaylist_table")
    fun getTracks(): Flow<List<TrackPlaylistEntity>>

    @Query("DELETE FROM trackplaylist_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)
}