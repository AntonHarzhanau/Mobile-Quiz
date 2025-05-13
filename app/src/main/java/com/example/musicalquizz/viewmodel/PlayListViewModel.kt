package com.example.musicalquizz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicalquizz.db.AppDatabase
import com.example.musicalquizz.db.entities.PlaylistEntity
import com.example.musicalquizz.db.entities.PlaylistTrackEntity
import com.example.musicalquizz.db.repository.PlaylistRepository
import com.example.musicalquizz.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(app: Application) : AndroidViewModel(app) {
    private val db   = AppDatabase.getInstance(app)
    private val repo = PlaylistRepository(db, app)

    /** All playlists from DB */
    val playlists: LiveData<List<PlaylistEntity>> =
        repo.playlists.asLiveData()

    /** Tracks for playlist from DB */
    fun tracks(playlistId: Long): LiveData<List<PlaylistTrackEntity>> =
        repo.tracks(playlistId).asLiveData()

    /** Create new playlist */
    fun createPlaylist(
        title: String,
        description: String,
        coverUri: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createPlaylist(title, description, coverUri)
        }
    }
    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletePlaylist(playlist)
        }
    }

    /** Add track to playlist */
    fun addTrack(playlistId: Long, track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addTrackToPlaylist(playlistId, track)
        }
    }

    /** Add tracks to playlist from album */
    fun addTracks(playlistId: Long, tracks: List<Track>) {
        viewModelScope.launch(Dispatchers.IO) {
            tracks.forEach { repo.addTrackToPlaylist(playlistId, it) }
        }
    }
}
