package com.practicum.playlistmaker.search.domain.impl

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.ExecutorService

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: ExecutorService,
    private val handler:Handler
    ) : TracksInteractor {

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