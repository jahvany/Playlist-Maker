package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val appLink: String,
    private val termsLink: String,
    private val supportEmail: EmailData
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(appLink)
    }

    override fun openTerms() {
        externalNavigator.openLink(termsLink)
    }

    override fun openSupport() {
        externalNavigator.openEmail(supportEmail)
    }
}