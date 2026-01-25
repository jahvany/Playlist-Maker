package com.practicum.playlistmaker.media.di


import com.practicum.playlistmaker.media.data.FavoriteRepositoryImpl
import com.practicum.playlistmaker.media.data.converters.TrackDbConverter
import com.practicum.playlistmaker.media.domain.db.FavoriteRepository
import org.koin.dsl.module

val mediaRepositoryModule = module {
    factory { TrackDbConverter() }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }
}