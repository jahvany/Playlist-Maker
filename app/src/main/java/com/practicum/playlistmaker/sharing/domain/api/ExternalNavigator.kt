package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(link: String) {
    }
    fun openLink(url: String) {
    }
    fun openEmail(data: EmailData) {
    }
}