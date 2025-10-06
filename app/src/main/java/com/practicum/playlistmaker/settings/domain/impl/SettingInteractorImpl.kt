package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        val isDark = repository.getTheme()
        return ThemeSettings(isDark)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.saveTheme(settings.isDarkTheme)
    }
}