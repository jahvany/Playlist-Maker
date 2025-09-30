package com.practicum.playlistmaker.settings.domain.impl

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    context: Context
) : SettingsInteractor {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getThemeSettings(): ThemeSettings {
        val isDark = prefs.getBoolean(KEY_DARK_THEME, false)
        return ThemeSettings(isDark)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        prefs.edit()
            .putBoolean(KEY_DARK_THEME, settings.isDarkTheme)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
    }
}