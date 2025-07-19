package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val PREFS_NAME = "SharedPreferences"
        const val KEY_THEME = "theme"
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(KEY_THEME, false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    var darkTheme = true
        private set

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(KEY_THEME,darkTheme)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}