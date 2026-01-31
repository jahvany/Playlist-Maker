package com.practicum.playlistmaker.player.domain.model

data class PlayerState(
    val status: PlayerStatus = PlayerStatus.DEFAULT,
    val timer: String = "00:00",
    val isFavorite: Boolean = false
)