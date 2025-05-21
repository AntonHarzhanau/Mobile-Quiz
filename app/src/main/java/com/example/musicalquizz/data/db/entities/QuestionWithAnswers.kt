package com.example.musicalquizz.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class QuestionWithAnswers(
    @Embedded
    val question: QuestionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "questionId",
        entity = AnswerEntity::class
    )
    val answers: List<AnswerEntity>
)