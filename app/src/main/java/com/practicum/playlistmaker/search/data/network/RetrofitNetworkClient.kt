package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.iTunesRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val imdbService: iTunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is iTunesRequest) return Response().apply { resultCode = 400 }

        return withContext(Dispatchers.IO) {
            try {
                val resp = imdbService.search(dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }
}