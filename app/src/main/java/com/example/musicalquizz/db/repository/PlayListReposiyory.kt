package com.example.musicalquizz.db.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.musicalquizz.db.AppDatabase
import com.example.musicalquizz.db.entities.PlaylistEntity
import com.example.musicalquizz.db.entities.PlaylistTrackEntity
import com.example.musicalquizz.db.worker.PreviewDownloadWorker
import com.example.musicalquizz.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(
    private val db: AppDatabase,
    private val context: Context
) {

    val playlists: Flow<List<PlaylistEntity>> =
        db.playlistDao().getAll()

    fun tracks(plId: Long): Flow<List<PlaylistTrackEntity>> =
        db.playlistTrackDao().getByPlaylist(plId)

    fun createPlaylist(
        title: String,
        desc: String,
        coverUri: String?
    ): Long {
        val pl = PlaylistEntity(
            title       = title,
            description = desc,
            coverUri    = coverUri
        )
        return db.playlistDao().insert(pl)
    }
    fun deletePlaylist(playList: PlaylistEntity) {
        db.playlistDao().delete(playList)
    }
    fun addTrackToPlaylist(plId: Long, track: Track) {

        val exists = db.playlistTrackDao().getByPlaylistAndTrack(plId, track.id)
        if (exists != null) return

        // 1) insert Entity to Playlist
        val entity = PlaylistTrackEntity(
            playlistId    = plId,
            trackId       = track.id,
            title         = track.title,
            artistName    = track.artist.name,
            albumCoverUrl = track.album.cover,
            previewUrl    = track.preview
        )
        val uid = db.playlistTrackDao().insert(entity)

        // 2) planning the task of loading previews in WorkManager
        val data = workDataOf(
            "uid" to uid,
            "previewUrl" to track.preview
        )
        val req = OneTimeWorkRequestBuilder<PreviewDownloadWorker>()
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(req)
    }

    /**
     * Updates the localPreviewPath field in the DB,
     * when the preview is downloaded by PreviewDownloadWorker.
     */
    fun updateLocalPath(uid: Long, path: String) {
        // getByUid — synchronous call, but still in the background in Worker
        val trackEntity = db.playlistTrackDao().getByUid(uid)
        trackEntity?.let {
            val updated = it.copy(localPreviewPath = path)
            db.playlistTrackDao().update(updated)
        }
    }
}
