package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {

    single { MediaPlayer() }

    viewModel {(url: String?) ->
        PlayerViewModel(url, get(), get(), get())
    }
}