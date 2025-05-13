package com.example.musicalquizz.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicalquizz.db.entities.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert fun insert(track: PlaylistTrackEntity): Long
    @Update fun update(track: PlaylistTrackEntity)
    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :plId")
    fun getByPlaylist(plId: Long): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM playlist_tracks WHERE uid = :uid")
    fun getByUid(uid: Long): PlaylistTrackEntity?

    @Query("""
      SELECT * FROM playlist_tracks 
      WHERE playlistId = :plId AND trackId = :trackId
    """)
    fun getByPlaylistAndTrack(plId: Long, trackId: Long): PlaylistTrackEntity?

    @Delete
    fun delete(track: PlaylistTrackEntity)

    @Query("""
        DELETE FROM playlist_tracks
        WHERE playlistId = :playlistId AND trackId = :trackId
    """)
    fun deleteByPlaylistAndTrack(playlistId: Long, trackId: Long)

}