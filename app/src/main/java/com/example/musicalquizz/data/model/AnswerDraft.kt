package com.example.musicalquizz.data.model

import com.example.musicalquizz.data.db.entities.AnswerEntity

data class AnswerDraft(
    val id: Long = System.currentTimeMillis(),
    val questionId: Long = 0L,
    var answerText: String = "",
    var isCorrect: Boolean = false
) {
    companion object {
        fun fromEntity(e: AnswerEntity): AnswerDraft =
            AnswerDraft(
                id = e.id,
                questionId = e.questionId,
                answerText = e.answerText,
                isCorrect  = e.isCorrect
            )
    }

    fun toEntity(questionId: Long): AnswerEntity =
        AnswerEntity(
            id         = this.id,
            questionId = questionId,
            answerText = this.answerText,
            isCorrect  = this.isCorrect
        )
}
