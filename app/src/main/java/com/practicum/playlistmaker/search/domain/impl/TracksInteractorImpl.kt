package com.practicum.playlistmaker.search.domain.impl

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    private val handler = Handler(Looper.getMainLooper())

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(expression)
                handler.post {
                    consumer.consume(tracks)
                }
            } catch (e: Exception) {
                handler.post {
                    consumer.onFailure(e)
                }
            }
        }
    }
}