package com.practicum.playlistmaker.sharing.di

import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

val sharingDataModule = module {

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

}