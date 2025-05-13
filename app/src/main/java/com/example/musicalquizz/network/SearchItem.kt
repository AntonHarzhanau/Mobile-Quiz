package com.example.musicalquizz.network

import com.example.musicalquizz.model.Album
import com.example.musicalquizz.model.Track

sealed class SearchItem {
    data class TrackItem(val track: Track) : SearchItem()
    data class AlbumItem(val album: Album) : SearchItem()
}