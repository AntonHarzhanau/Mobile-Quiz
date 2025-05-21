package com.example.musicalquizz.data.db.dao

import androidx.room.*
import com.example.musicalquizz.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getAll(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: PlaylistEntity): Long

    @Update
    fun update(playlist: PlaylistEntity)

    @Delete
    fun delete(playlist: PlaylistEntity)
}