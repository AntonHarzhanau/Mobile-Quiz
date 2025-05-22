package com.example.musicalquizz.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.QuizWithQuestions

import kotlinx.coroutines.launch

class QuizDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).quizDao()

    private val _quiz = MutableLiveData<QuizWithQuestions>()
    val quiz: LiveData<QuizWithQuestions> = _quiz

    /** Call from fragment to load data */
    fun loadQuiz(id: Long) {
        viewModelScope.launch {
            dao.getQuizWithQuestionsOnce(id)?.let { result ->
                Log.d("QuizDetailsVM", "Quiz $id loaded with ${result.questions.size} questions")
                _quiz.postValue(result)
            }
        }
    }
}

