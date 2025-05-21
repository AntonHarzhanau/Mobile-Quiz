package com.example.musicalquizz.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class QuizWithQuestions(
    @Embedded
    val quiz: QuizEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "quizId",
        entity = QuestionEntity::class
    )
    val questions: List<QuestionEntity>
)