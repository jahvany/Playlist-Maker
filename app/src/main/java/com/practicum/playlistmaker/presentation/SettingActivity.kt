package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.data.App
import com.practicum.playlistmaker.R

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val titleSettings = findViewById<MaterialToolbar>(R.id.titleSettings)
        titleSettings.setNavigationOnClickListener { finish() }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitch)

        themeSwitch.isChecked = (applicationContext as App).darkTheme

        themeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            }

        val shareMessage = getString(R.string.messageAddress)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }

        val supportSubject = getString(R.string.supportSubject)
        val supportMessage = getString(R.string.supportMessage)
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myAddress)))
            putExtra(Intent.EXTRA_SUBJECT, supportSubject)
            putExtra(Intent.EXTRA_TEXT, supportMessage)
        }

        val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.webSite))
        }

        val shareButton = findViewById<MaterialTextView>(R.id.shareButton)
        shareButton.setOnClickListener { startActivity(shareIntent) }

        val supportButton = findViewById<MaterialTextView>(R.id.supportButton)
        supportButton.setOnClickListener { startActivity(supportIntent) }

        val agreementButton = findViewById<MaterialTextView>(R.id.agreementButton)
        agreementButton.setOnClickListener { startActivity(agreementIntent) }
    }
}

