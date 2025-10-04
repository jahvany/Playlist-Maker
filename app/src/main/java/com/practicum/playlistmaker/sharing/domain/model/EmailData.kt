package com.practicum.playlistmaker.sharing.domain.model

data class EmailData(
    val emails: Array<String>,
    val subject: String,
    val message: String
)