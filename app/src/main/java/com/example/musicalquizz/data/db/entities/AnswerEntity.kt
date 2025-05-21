package com.example.musicalquizz.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns  = ["questionId"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("questionId")
    ]
)
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    var answerText: String,
    var isCorrect: Boolean
)