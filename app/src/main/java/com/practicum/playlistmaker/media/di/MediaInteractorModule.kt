package com.practicum.playlistmaker.media.di


import com.practicum.playlistmaker.media.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.impl.FavoriteInteractorImpl
import com.practicum.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import org.koin.dsl.module

val mediaInteractorModule = module {
    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}