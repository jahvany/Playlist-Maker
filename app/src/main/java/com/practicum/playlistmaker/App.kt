package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.util.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(applicationContext)

        val settingsInteractor = Creator.provideSettingsInteractor()
        val darkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val settingsInteractor = Creator.provideSettingsInteractor()
        settingsInteractor.updateThemeSetting(ThemeSettings(darkThemeEnabled))

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}