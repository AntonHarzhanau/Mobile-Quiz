package com.example.musicalquizz.data.db.repository

import androidx.lifecycle.LiveData
import com.example.musicalquizz.data.db.dao.QuestionDao
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.entities.QuestionEntity
import com.example.musicalquizz.data.db.entities.QuestionWithAnswers

class QuestionRepository(private val dao: QuestionDao) {

    /** All questions with answers for this quiz */
    fun getQuestionsForQuiz(quizId: Long): LiveData<List<QuestionWithAnswers>> =
        dao.getQuestionsForQuiz(quizId)

    /** Insert new question */
    suspend fun insert(question: QuestionEntity): Long =
        dao.insertQuestion(question)

    /** Update an existing question */
    suspend fun update(question: QuestionEntity) =
        dao.updateQuestion(question)

    /** Delete question (cascade will delete answers too) */
    suspend fun delete(question: QuestionEntity) =
        dao.deleteQuestion(question)

    /** Insert a list of answers (new or updated) */
    suspend fun insertAll(answers: List<AnswerEntity>) =
        dao.insertAnswers(answers)
}