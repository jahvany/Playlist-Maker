package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface SharingInteractor {
    fun shareApp(appLink:String)
    fun openTerms(termsLink: String)
    fun openSupport(supportEmail: EmailData)
}