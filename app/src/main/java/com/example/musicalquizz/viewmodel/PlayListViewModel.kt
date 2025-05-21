package com.example.musicalquizz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.PlaylistEntity
import com.example.musicalquizz.data.db.entities.PlaylistTrackEntity
import com.example.musicalquizz.data.db.repository.PlaylistRepository
import com.example.musicalquizz.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PlaylistRepository(
        AppDatabase.getInstance(application),
        application
    )

    // 1) Flow from DAO that runs immediately (Eagerly) and caches the latest list
    private val playlistsFlow = repo.playlists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // 2) LiveData для UI
    val playlists: LiveData<List<PlaylistEntity>> =
        playlistsFlow.asLiveData()

    /** Tracks for a given playlist */
    fun tracks(playlistId: Long): LiveData<List<PlaylistTrackEntity>> =
        repo.tracks(playlistId).asLiveData()

    /** Create new playlist */
    fun createPlaylist(title: String, description: String, coverUri: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createPlaylist(title, description, coverUri)
        }
    }

    /** Delete existing playlist */
    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletePlaylist(playlist)
        }
    }

    /** Add single track to playlist */
    fun addTrack(playlistId: Long, track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addTrackToPlaylist(playlistId, track)
        }
    }

    /** Add multiple tracks to playlist */
    fun addTracks(playlistId: Long, tracks: List<Track>) {
        viewModelScope.launch(Dispatchers.IO) {
            tracks.forEach { repo.addTrackToPlaylist(playlistId, it) }
        }
    }
}