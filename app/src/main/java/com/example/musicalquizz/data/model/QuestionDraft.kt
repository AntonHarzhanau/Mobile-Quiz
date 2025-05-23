package com.example.musicalquizz.data.model

import com.example.musicalquizz.data.db.entities.QuestionEntity

data class QuestionDraft(
    val trackId: Long,
    val trackTitle: String,
    val trackArtist: String,
    val trackCoverUrl: String,
    val trackPreviewUrl: String,
    var questionText: String = "",
    var answers: MutableList<AnswerDraft> = mutableListOf()
) {
    fun toEntity(quizId: Long): QuestionEntity =
        QuestionEntity(
            id              = 0L,
            quizId          = quizId,
            trackId         = trackId,
            trackTitle      = trackTitle,
            trackArtist     = trackArtist,
            trackCoverUrl   = trackCoverUrl,
            trackPreviewUrl = trackPreviewUrl,
            questionText    = questionText
        )
}

