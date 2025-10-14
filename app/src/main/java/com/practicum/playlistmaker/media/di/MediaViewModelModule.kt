package com.practicum.playlistmaker.media.di

import com.practicum.playlistmaker.media.ui.view_model.MediaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaViewModelModule = module {

    viewModel {
        MediaViewModel()
    }
}