package com.example.musicalquizz.data.model

data class QuestionDraft(
    val trackId: Long,
    val trackTitle: String,
    val trackArtist: String,
    val trackCoverUrl: String,
    val trackPreviewUrl: String,
    var questionText: String = "",
    var answers: MutableList<AnswerDraft> = mutableListOf()
)
