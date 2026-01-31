package com.practicum.playlistmaker.media.di

import androidx.room.Room
import com.practicum.playlistmaker.media.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaDataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}