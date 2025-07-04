package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val titleSettings = findViewById<MaterialToolbar>(R.id.titleSettings)
        titleSettings.setNavigationOnClickListener { finish() }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitch)
        val isDark = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> {
                val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                uiMode == Configuration.UI_MODE_NIGHT_YES
            }

            else -> false
        }
        themeSwitch.isChecked = isDark
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
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

