package com.example.musicalquizz.data.model

data class Quiz(
    val id: Long,
    val coverUri: String,
    val title: String,
    val albumName: String,
    val questionCount: Int
)