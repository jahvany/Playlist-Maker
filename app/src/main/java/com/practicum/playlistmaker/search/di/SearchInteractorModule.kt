package com.practicum.playlistmaker.search.di

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val searchInteractorModule = module {

    single<ExecutorService>{
        Executors.newCachedThreadPool()
    }

    single<Handler>{
        Handler(Looper.getMainLooper())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get(), get(), get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

}