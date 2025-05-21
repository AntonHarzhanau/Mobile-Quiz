package com.example.musicalquizz.data.network

import com.example.musicalquizz.data.model.Album
import com.example.musicalquizz.data.model.Track

sealed class SearchItem {
    data class TrackItem(val track: Track) : SearchItem()
    data class AlbumItem(val album: Album) : SearchItem()
}