package com.example.musicalquizz.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.data.db.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao  = AppDatabase.getInstance(application).quizDao()
    private val repo = QuizRepository(application.applicationContext)

    val quizzes: LiveData<List<QuizEntity>> = dao.getAllQuizzes()

    fun deleteQuiz(quiz: QuizEntity) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(quiz)
    }
}
