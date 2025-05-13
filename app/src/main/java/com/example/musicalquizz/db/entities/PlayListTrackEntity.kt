package com.example.musicalquizz.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_tracks",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns  = ["playlistId"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [ Index("playlistId"), Index("trackId") ]
)
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val playlistId: Long,
    val trackId: Long,
    val title: String,
    val artistName: String,
    val albumCoverUrl: String,
    val previewUrl: String,
    val localPreviewPath: String? = null
)
