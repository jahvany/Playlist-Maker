package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val prefs: SharedPreferences) : SettingsRepository {

    override fun getTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, false)

    override fun saveTheme(isDark: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_THEME, isDark)
            .apply()
    }

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
