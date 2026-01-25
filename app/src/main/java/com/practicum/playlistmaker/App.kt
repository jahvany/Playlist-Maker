package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.media.di.mediaDataModule
import com.practicum.playlistmaker.media.di.mediaInteractorModule
import com.practicum.playlistmaker.media.di.mediaRepositoryModule
import com.practicum.playlistmaker.media.di.mediaViewModelModule
import com.practicum.playlistmaker.player.di.playerViewModelModule
import com.practicum.playlistmaker.search.di.searchDataModule
import com.practicum.playlistmaker.search.di.searchInteractorModule
import com.practicum.playlistmaker.search.di.searchRepositoryModule
import com.practicum.playlistmaker.search.di.searchViewModelModule
import com.practicum.playlistmaker.settings.di.settingsInteractorModule
import com.practicum.playlistmaker.settings.di.settingsRepositoryModule
import com.practicum.playlistmaker.settings.di.settingsViewModelModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.di.sharingDataModule
import com.practicum.playlistmaker.sharing.di.sharingInteractorModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(playerViewModelModule,
                searchDataModule,
                searchRepositoryModule,
                searchInteractorModule,
                searchViewModelModule,
                settingsRepositoryModule,
                settingsInteractorModule,
                settingsViewModelModule,
                sharingDataModule,
                sharingInteractorModule,
                mediaDataModule,
                mediaRepositoryModule,
                mediaInteractorModule,
                mediaViewModelModule
            )
        }

        val settingsInteractor: SettingsInteractor = getKoin().get()
        val darkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val settingsInteractor: SettingsInteractor = getKoin().get()
        settingsInteractor.updateThemeSetting(ThemeSettings(darkThemeEnabled))

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}