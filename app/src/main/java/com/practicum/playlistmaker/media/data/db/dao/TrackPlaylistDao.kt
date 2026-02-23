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
    @Query("SELECT * FROM trackplaylist_table WHERE playlistId = :playlistId ORDER BY id DESC")
    fun getTracks(playlistId: Int): Flow<List<TrackPlaylistEntity>>
    @Query("DELETE FROM trackplaylist_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrack(playlistId: Int, trackId: Int)
}