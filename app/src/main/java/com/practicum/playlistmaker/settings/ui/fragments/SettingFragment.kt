package com.practicum.playlistmaker.settings.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.ui.compose.SettingsScreen
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.util.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val isDarkThemeEnabled = viewModel.darkTheme.value ?: false

        val onThemeToggle = { switcherChecked: Boolean ->
            viewModel.switchTheme(switcherChecked)
            (requireContext().applicationContext as App).switchTheme(switcherChecked)
        }

        val onShareClick = {
            val appLink = getString(R.string.messageAddress)
            viewModel.shareApp(appLink)
        }

        val onSupportClick = {
            val supportEmail = EmailData(
                emails = arrayOf(getString(R.string.myAddress)),
                subject = getString(R.string.supportSubject),
                message = getString(R.string.supportMessage)
            )
            viewModel.openSupport(supportEmail)
        }

        val onAgreementClick = {
            val termsLink = getString(R.string.webSite)
            viewModel.openTerms(termsLink)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme {
                    SettingsScreen(
                        isDarkThemeEnabled,
                        onThemeToggle,
                        onShareClick,
                        onSupportClick,
                        onAgreementClick
                    )
                }
            }
        }
    }
}