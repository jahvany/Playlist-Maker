package com.practicum.playlistmaker.media.di


import com.practicum.playlistmaker.media.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.media.data.db.FavoriteRepositoryImpl
import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.data.converters.TrackPlaylistDbConverter
import com.practicum.playlistmaker.media.data.db.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.domain.db.FavoriteRepository
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import org.koin.dsl.module

val mediaRepositoryModule = module {
    factory { TrackDbConverter() }

    factory { PlaylistDbConverter() }

    factory { TrackPlaylistDbConverter() }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
}