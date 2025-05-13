package com.example.musicalquizz.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicalquizz.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert fun insert(pl: PlaylistEntity): Long
    @Update fun update(pl: PlaylistEntity)
    @Query("SELECT * FROM playlists") fun getAll(): Flow<List<PlaylistEntity>>
    @Query("SELECT * FROM playlists WHERE id = :id") fun getById(id: Long): PlaylistEntity?
    @Delete fun delete(pl: PlaylistEntity)
}