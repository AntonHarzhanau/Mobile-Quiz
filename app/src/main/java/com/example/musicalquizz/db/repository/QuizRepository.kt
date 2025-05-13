package com.example.musicalquizz.db.repository

import com.example.musicalquizz.db.dao.QuizDao
import com.example.musicalquizz.db.entities.QuizEntity
import com.example.musicalquizz.model.Quiz

class QuizRepository(private val dao: QuizDao){
    val quizzes = dao.getAll()

    suspend fun createQuiz(entity: QuizEntity): Long =
        dao.insert(entity)

    suspend fun deleteQuiz(entity: QuizEntity) =
        dao.delete(entity)

    fun getQuizLive(id: Long) = dao.getById(id)

    suspend fun getQuizEntity(id: Long) = dao.getEntityById(id)
}

