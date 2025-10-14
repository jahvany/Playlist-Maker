package com.practicum.playlistmaker.settings.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val titleSettings = findViewById<MaterialToolbar>(R.id.titleSettings)
        titleSettings.setNavigationOnClickListener { finish() }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitch)

        themeSwitch.isChecked = viewModel.darkTheme.value ?: false

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        findViewById<MaterialTextView>(R.id.shareButton).setOnClickListener {
            val appLink = getString(R.string.messageAddress)
            viewModel.shareApp(appLink)
        }

        findViewById<MaterialTextView>(R.id.supportButton).setOnClickListener {
            val supportEmail = EmailData(
                emails = arrayOf(getString(R.string.myAddress)),
                subject = getString(R.string.supportSubject),
                message = getString(R.string.supportMessage)
            )
            viewModel.openSupport(supportEmail)
        }

        findViewById<MaterialTextView>(R.id.agreementButton).setOnClickListener {
            findViewById<MaterialTextView>(R.id.agreementButton).setOnClickListener {
                val termsLink = getString(R.string.webSite)
                viewModel.openTerms(termsLink)
            }
        }

        viewModel.darkTheme.observe(this) { isDark ->
            themeSwitch.isChecked = isDark
            (applicationContext as App).switchTheme(isDark)
        }
    }
}