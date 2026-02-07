package com.practicum.playlistmaker.media.di

import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.MediaViewModel
import com.practicum.playlistmaker.media.ui.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaViewModelModule = module {

    viewModel {
        MediaViewModel()
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    viewModel {
        PlaylistViewModel(get())
    }
    viewModel {
        NewPlaylistViewModel(get())
    }
}