package com.practicum.playlistmaker.settings.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsRepositoryModule = module {

    single {
        androidContext()
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

}