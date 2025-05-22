package com.example.musicalquizz.data.db.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.entities.QuestionEntity
import com.example.musicalquizz.data.db.entities.QuestionWithAnswers


@Dao
interface QuestionDao {
    @Transaction
    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    fun getQuestionsForQuiz(quizId: Long): LiveData<List<QuestionWithAnswers>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId")
    suspend fun countQuestionsForQuiz(quizId: Long): Int

    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId AND trackId = :trackId")
    suspend fun countQuestionsForQuizTrack(quizId: Long, trackId: Long): Int

    @Transaction
    @Query("""
    SELECT * 
      FROM questions 
     WHERE quizId = :quizId AND trackId = :trackId 
     LIMIT 1
  """)
    fun getQuestionWithAnswersOnce(quizId: Long, trackId: Long): QuestionWithAnswers?
}