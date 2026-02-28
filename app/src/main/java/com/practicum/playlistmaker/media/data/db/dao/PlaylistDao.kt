package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Int): Flow<PlaylistEntity?>

    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)
}