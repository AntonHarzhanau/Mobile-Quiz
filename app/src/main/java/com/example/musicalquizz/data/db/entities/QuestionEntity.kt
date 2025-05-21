package com.example.musicalquizz.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns  = ["quizId"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("quizId"),
        Index("trackId")
    ]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizId: Long,
    val trackId: Long,
    val trackTitle: String,
    val trackArtist: String,
    val trackCoverUrl: String,
    val trackPreviewUrl: String,
    val questionText: String
)