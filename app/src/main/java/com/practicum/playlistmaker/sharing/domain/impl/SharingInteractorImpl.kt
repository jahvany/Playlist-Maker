package com.practicum.playlistmaker.sharing.domain.impl

import android.content.res.Resources
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val resources: Resources
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return resources.getString(R.string.messageAddress)
    }

    private fun getSupportEmailData(): EmailData {
        val email = resources.getString(R.string.myAddress)
        val subject = resources.getString(R.string.supportSubject)
        val message = resources.getString(R.string.supportMessage)

        return EmailData(
            emails = arrayOf(email),
            subject = subject,
            message = message
        )
    }
    private fun getTermsLink(): String {
        return resources.getString(R.string.webSite)
    }
}