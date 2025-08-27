package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

class iTunesResponse(
    val results: List<Track>
) : Response()