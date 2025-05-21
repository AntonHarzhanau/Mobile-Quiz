package com.example.musicalquizz.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicalquizz.data.db.dao.AnswerDao
import com.example.musicalquizz.data.db.dao.PlaylistDao
import com.example.musicalquizz.data.db.dao.PlaylistTrackDao
import com.example.musicalquizz.data.db.dao.QuestionDao
import com.example.musicalquizz.data.db.dao.QuizDao
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.entities.PlaylistEntity
import com.example.musicalquizz.data.db.entities.PlaylistTrackEntity
import com.example.musicalquizz.data.db.entities.QuestionEntity
import com.example.musicalquizz.data.db.entities.QuizEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        QuizEntity::class,
        QuestionEntity::class,
        AnswerEntity::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao

    companion object {
        // singleton Room database
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "musical_quiz.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { INSTANCE = it }
            }
    }
}