package com.practicum.playlistmaker.settings.ui.activity


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.data.App
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

class SettingActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.getFactory(
            SharingInteractorImpl(ExternalNavigatorImpl(this), resources),
            SettingsInteractorImpl(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val titleSettings = findViewById<MaterialToolbar>(R.id.titleSettings)
        titleSettings.setNavigationOnClickListener { finish() }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitch)
        val settingsInteractor = SettingsInteractorImpl(this)
        themeSwitch.isChecked = settingsInteractor.getThemeSettings().isDarkTheme

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        findViewById<MaterialTextView>(R.id.shareButton).setOnClickListener {
            viewModel.shareApp()
        }

        findViewById<MaterialTextView>(R.id.supportButton).setOnClickListener {
            viewModel.openSupport()
        }

        findViewById<MaterialTextView>(R.id.agreementButton).setOnClickListener {
            viewModel.openTerms()
        }

        viewModel.darkTheme.observe(this) { isDark ->
            themeSwitch.isChecked = isDark
            (applicationContext as App).switchTheme(isDark)
        }
    }
}