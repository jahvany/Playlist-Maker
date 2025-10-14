package com.practicum.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.storage.StorageClient
import java.lang.reflect.Type
import androidx.core.content.edit

class PrefsStorageClient<T>(
    private val dataKey: String,
    private val type: Type,
    private val prefs: SharedPreferences,
    private val gson: Gson) : StorageClient<T> {

    override fun storeData(data: T) {
        prefs.edit { putString(dataKey, gson.toJson(data, type)) }
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }
}