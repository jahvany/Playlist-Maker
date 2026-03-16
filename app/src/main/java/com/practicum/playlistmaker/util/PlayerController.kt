package com.practicum.playlistmaker.util

import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val stateFlow: StateFlow<PlayerState>

    fun prepare(url: String?)

    fun getCurrentState(): PlayerState

    fun setTrack(track: Track)

    fun playbackControl()

    fun observeState(): StateFlow<PlayerState>

    fun showNotification()

    fun hideNotification()

    fun stopPlayer()
}