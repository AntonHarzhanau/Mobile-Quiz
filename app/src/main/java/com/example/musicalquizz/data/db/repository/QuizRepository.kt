package com.example.musicalquizz.data.db.repository


import androidx.lifecycle.LiveData
import com.example.musicalquizz.data.db.dao.QuizDao
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.data.db.entities.QuizWithQuestions


class QuizRepository(private val dao: QuizDao) {

    /** All quizzes with their questions */
    val allQuizzes: LiveData<List<QuizWithQuestions>> =
        dao.getAllQuizWithQuestions()

    /** One quiz + its questions by ID */
    fun getQuizWithQuestions(id: Long): LiveData<QuizWithQuestions> =
        dao.getQuizWithQuestions(id)

    /** Insert new (or update if PK matches) */
    suspend fun insert(quiz: QuizEntity): Long =
        dao.insertQuiz(quiz)

    /** Update existing */
    suspend fun update(quiz: QuizEntity) =
        dao.updateQuiz(quiz)

    /** Delete (cascade will delete related questions too) */
    suspend fun delete(quiz: QuizEntity) =
        dao.deleteQuiz(quiz)
}


