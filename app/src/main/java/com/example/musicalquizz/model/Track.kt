package com.example.musicalquizz.model

import kotlin.time.Duration

data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val duration: String,
    val preview: String

)

