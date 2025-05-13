package com.example.musicalquizz.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coverUri: String,
    val title: String,
    val albumName: String,
    val questionCount: Int
)