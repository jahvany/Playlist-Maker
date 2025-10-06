package com.practicum.playlistmaker.sharing.di

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingInteractorModule = module {

    single<SharingInteractor> {
        val context = androidContext()
        val resources = context.resources

        val appLink = resources.getString(R.string.messageAddress)
        val termsLink = resources.getString(R.string.webSite)

        val supportEmail = EmailData(
            emails = arrayOf(resources.getString(R.string.myAddress)),
            subject = resources.getString(R.string.supportSubject),
            message = resources.getString(R.string.supportMessage)
        )

        SharingInteractorImpl(
            externalNavigator = get(),
            appLink = appLink,
            termsLink = termsLink,
            supportEmail = supportEmail
        )
    }

}