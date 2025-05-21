package com.example.musicalquizz.data.model

data class Playlist(
    val id: Long,
    var title: String,
    var description: String,
    var coverUri: String?,
    val trackIds: MutableList<Long>
)
