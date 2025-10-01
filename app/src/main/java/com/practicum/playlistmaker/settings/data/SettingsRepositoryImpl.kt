package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, false)

    override fun saveTheme(isDark: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_THEME, isDark)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
