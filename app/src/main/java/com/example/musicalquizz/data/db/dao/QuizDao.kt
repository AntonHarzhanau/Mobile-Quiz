package com.example.musicalquizz.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.data.db.entities.QuizWithQuestions


@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): LiveData<List<QuizEntity>>

    @Transaction
    @Query("SELECT * FROM quizzes")
    fun getAllQuizWithQuestions(): LiveData<List<QuizWithQuestions>>

    @Transaction
    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizWithQuestionsOnce(id: Long): QuizWithQuestions?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Update
    suspend fun updateQuiz(quiz: QuizEntity)

    @Delete
    suspend fun deleteQuiz(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizEntity(id: Long): QuizEntity?

    @Query("SELECT * FROM quizzes WHERE id = :id")
    fun getQuizLive(id: Long): LiveData<QuizEntity?>

    @Transaction
    @Query("SELECT * FROM quizzes WHERE id = :id")
    fun getQuizWithQuestions(id: Long): LiveData<QuizWithQuestions>

    @Query("UPDATE quizzes SET questionCount = :count WHERE id = :quizId")
    suspend fun updateQuizQuestionCount(quizId: Long, count: Int)

    @Query("UPDATE quizzes SET questionCount = :count WHERE id = :quizId")
    suspend fun updateQuestionCount(quizId: Long, count: Int)



}