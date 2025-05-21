package com.example.musicalquizz.data.db.repository

import androidx.lifecycle.LiveData
import com.example.musicalquizz.data.db.dao.AnswerDao
import com.example.musicalquizz.data.db.entities.AnswerEntity


class AnswerRepository(private val dao: AnswerDao) {

    /** All responses for this question */
    fun getAnswersForQuestion(questionId: Long): LiveData<List<AnswerEntity>> =
        dao.getAnswersForQuestion(questionId)

    /** Insert a list of answers (new or updated) */
    suspend fun insertAll(answers: List<AnswerEntity>) =
        dao.insertAnswers(answers)

    /** Delete one answer */
    suspend fun delete(answer: AnswerEntity) =
        dao.deleteAnswer(answer)
}