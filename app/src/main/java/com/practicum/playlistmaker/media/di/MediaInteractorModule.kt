package com.practicum.playlistmaker.media.di


import com.practicum.playlistmaker.media.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.media.domain.impl.FavoriteInteractorImpl
import org.koin.dsl.module

val mediaInteractorModule = module {
    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }
}