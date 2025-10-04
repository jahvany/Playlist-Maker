package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun getTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}