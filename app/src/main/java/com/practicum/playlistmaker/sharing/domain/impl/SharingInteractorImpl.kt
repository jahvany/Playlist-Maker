package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp(appLink:String) {
        externalNavigator.shareLink(appLink)
    }

    override fun openTerms(termsLink: String) {
        externalNavigator.openLink(termsLink)
    }

    override fun openSupport(supportEmail: EmailData) {
        externalNavigator.openEmail(supportEmail)
    }
}