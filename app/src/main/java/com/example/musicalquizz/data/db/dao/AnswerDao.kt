package com.example.musicalquizz.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musicalquizz.data.db.entities.AnswerEntity


@Dao
interface AnswerDao {
    @Query("SELECT * FROM answers WHERE questionId = :questionId")
    fun getAnswersForQuestion(questionId: Long): LiveData<List<AnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)

    @Delete
    suspend fun deleteAnswer(answer: AnswerEntity)
}