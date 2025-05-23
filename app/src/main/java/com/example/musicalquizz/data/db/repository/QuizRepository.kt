package com.example.musicalquizz.data.db.repository


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.data.db.entities.QuizWithQuestions
import com.example.musicalquizz.data.model.QuestionDraft

class QuizRepository(context: Context) {

    private val db: AppDatabase = AppDatabase.getInstance(context.applicationContext)

    // все три DAO
    private val quizDao     = db.quizDao()
    private val questionDao = db.questionDao()
    private val answerDao   = db.answerDao()

    val allQuizzes: LiveData<List<QuizWithQuestions>> =
        quizDao.getAllQuizWithQuestions()

    fun getQuizWithQuestions(id: Long): LiveData<QuizWithQuestions> =
        quizDao.getQuizWithQuestions(id)

    suspend fun insert(quiz: QuizEntity): Long = quizDao.insertQuiz(quiz)
    suspend fun update(quiz: QuizEntity) = quizDao.updateQuiz(quiz)
    suspend fun delete(quiz: QuizEntity) = quizDao.deleteQuiz(quiz)
    suspend fun updateQuestionCount(quizId: Long, count: Int) =
        quizDao.updateQuestionCount(quizId, count)

    @Transaction
    suspend fun saveQuizWithQuestions(
        quiz: QuizEntity,
        drafts: List<QuestionDraft>
    ) {
        val quizId = if (quiz.id == 0L) {
            quizDao.insertQuiz(quiz)
        } else {
            quizDao.updateQuiz(quiz)
            quiz.id
        }

        drafts.forEach { draft ->
            val qEnt = draft.toEntity(quizId)
            val qId  = questionDao.insertQuestion(qEnt)
            answerDao.insertAnswers(draft.answers.map { it.toEntity(qId) })
        }

        val cnt = questionDao.countQuestionsForQuiz(quizId)
        quizDao.updateQuestionCount(quizId, cnt)
    }
}


