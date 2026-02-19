package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.media.data.db.entity.TrackPlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TrackPlaylistDbConverter {

    fun map(track: Track): TrackPlaylistEntity {
        return TrackPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(track: TrackPlaylistEntity): Track {
        return Track(track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

}