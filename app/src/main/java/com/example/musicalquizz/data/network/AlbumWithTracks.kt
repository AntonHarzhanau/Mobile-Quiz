package com.example.musicalquizz.data.network

import com.example.musicalquizz.data.model.Artist

data class AlbumWithTracks(
    val id: Long,
    val title: String,
    val cover: String,
    val artist: Artist,
    val tracks: TrackResponse
)