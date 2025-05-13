package com.example.musicalquizz.model

data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val duration: String,
    val preview: String

)

