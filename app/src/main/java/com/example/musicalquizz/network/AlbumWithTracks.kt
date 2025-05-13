package com.example.musicalquizz.network

import com.example.musicalquizz.model.Artist

data class AlbumWithTracks(
    val id: Long,
    val title: String,
    val cover: String,
    val artist: Artist,
    val tracks: TrackResponse
)