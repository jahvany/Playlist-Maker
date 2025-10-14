package com.practicum.playlistmaker.sharing.di

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingInteractorModule = module {

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}