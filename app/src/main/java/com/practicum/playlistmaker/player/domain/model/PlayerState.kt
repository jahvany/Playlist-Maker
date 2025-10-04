package com.practicum.playlistmaker.player.domain.model

sealed class PlayerState(open val timer: String = "00:00") {
        object Default : PlayerState()
        object Prepared : PlayerState()
        data class Playing(override val timer: String) : PlayerState()
        data class Paused(override val timer: String) : PlayerState()

    fun setTimer(newTimer: String): PlayerState = when (this) {
        is Default -> Default
        is Prepared -> Prepared
        is Playing -> copy(timer = newTimer)
        is Paused -> copy(timer = newTimer)
    }
}