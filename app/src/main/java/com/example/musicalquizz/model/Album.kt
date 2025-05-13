package com.example.musicalquizz.model

data class Album(
    val id: Long,
    val title: String,
    val artist: Artist,
    val cover: String
)