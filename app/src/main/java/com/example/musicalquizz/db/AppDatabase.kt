package com.example.musicalquizz.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicalquizz.db.dao.PlaylistDao
import com.example.musicalquizz.db.dao.PlaylistTrackDao
import com.example.musicalquizz.db.entities.PlaylistEntity
import com.example.musicalquizz.db.entities.PlaylistTrackEntity

@Database(
    entities = [PlaylistEntity::class, PlaylistTrackEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        // singleton Room database
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "musical_quiz.db"
                ).build().also { INSTANCE = it }
            }
    }
}