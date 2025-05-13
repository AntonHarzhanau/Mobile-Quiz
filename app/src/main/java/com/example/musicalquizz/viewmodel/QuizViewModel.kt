package com.example.musicalquizz.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.musicalquizz.db.AppDatabase
import com.example.musicalquizz.db.entities.QuizEntity
import com.example.musicalquizz.db.repository.QuizRepository
import com.example.musicalquizz.model.Quiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: QuizRepository

    val quizzes: LiveData<List<QuizEntity>>
    val quizDetail = MutableLiveData<QuizEntity?>()

    init {
        val dao = AppDatabase.getInstance(app).quizDao()
        repo = QuizRepository(dao)
        quizzes = repo.quizzes
    }

    fun loadQuiz(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val q = repo.getQuizEntity(id)
            quizDetail.postValue(q)
        }
    }

    fun createQuiz(coverUri: String, title: String, albumName: String, questionCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createQuiz(
                QuizEntity(
                    coverUri = coverUri,
                    title = title,
                    albumName = albumName,
                    questionCount = questionCount
                )
            )
        }
    }

    fun deleteQuiz(entity: QuizEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteQuiz(entity)
        }
    }
}
