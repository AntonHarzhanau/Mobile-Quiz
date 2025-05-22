package com.example.musicalquizz.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.entities.QuestionWithAnswers
import com.example.musicalquizz.data.db.repository.QuestionRepository
import com.example.musicalquizz.data.network.DeezerApi

import kotlinx.coroutines.launch

class QuizGameViewModel(
    private val questionRepo: QuestionRepository,
    private val quizId: Long
) : ViewModel() {

    // 1) Вся пара (вопрос+ответы)

    private val qaList: LiveData<List<QuestionWithAnswers>> =
        questionRepo.getQuestionsForQuiz(quizId).also { live ->
            live.observeForever { list ->
                Log.d("QuizGameVM", "Loaded questions for quiz $quizId: count = ${list.size}")
            }
        }
    // 2) Текущий индекс
    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    // 3) Выбранные ответы
    private val _selectedAnswers = MutableLiveData<Set<Long>>(emptySet())
    val selectedAnswers: LiveData<Set<Long>> = _selectedAnswers

    // 4) Флаг отправки
    private val _isSubmitted = MutableLiveData(false)
    val isSubmitted: LiveData<Boolean> = _isSubmitted

    // 5) Счёт правильных
    private var score = 0
    val correctCount: Int get() = score
    val totalCount: Int get() = qaList.value?.size ?: 0

    // 6) LiveData текущего вопроса
    val currentQA: LiveData<QuestionWithAnswers?> = MediatorLiveData<QuestionWithAnswers?>().apply {
        fun refresh() {
            val list = qaList.value
            val idx = _currentIndex.value ?: 0
            value = list?.getOrNull(idx)
        }
        addSource(qaList) { refresh() }
        addSource(_currentIndex) { refresh() }
    }

    // 7) Список ответов для UI
    val currentAnswers: LiveData<List<AnswerEntity>> =
        currentQA.map { it?.answers.orEmpty() }

    // 8) LiveData для свежей ссылки preview
    private val _previewUrl = MutableLiveData<String?>()
    val previewUrl: LiveData<String?> = _previewUrl

    /** Запрашивает свежий preview URL по trackId */
    fun loadPreview(trackId: Long) {
        viewModelScope.launch {
            try {
                val url = DeezerApi.retrofitService.getTrackById(trackId).preview
                _previewUrl.value = url
            } catch (e: Exception) {
                _previewUrl.value = null
            }
        }
    }

    /** Отметить/снять ответ */
    fun toggleAnswer(answerId: Long, checked: Boolean) {
        if (_isSubmitted.value == true) return
        _selectedAnswers.value = _selectedAnswers.value!!
            .toMutableSet()
            .apply { if (checked) add(answerId) else remove(answerId) }
    }

    /** Проверить ответ, обновить score и флаг отправки */
    fun submit(): Boolean {
        val qa = currentQA.value ?: return false
        val correctIds = qa.answers.filter { it.isCorrect }.map { it.id }.toSet()
        val isCorrect = correctIds == _selectedAnswers.value
        if (isCorrect) score++
        _isSubmitted.value = true
        return isCorrect
    }

    /** Пропустить — просто next() */
    fun skip(): Boolean = next()

    /** Перейти к следующему вопросу, сбросив выбор и флаг */
    fun next(): Boolean {
        val next = (_currentIndex.value ?: 0) + 1
        return if (qaList.value != null && next < qaList.value!!.size) {
            _currentIndex.value = next
            _selectedAnswers.value = emptySet()
            _isSubmitted.value = false
            true
        } else false
    }
}

class QuizGameViewModelFactory(
    private val questionRepo: QuestionRepository,
    private val quizId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizGameViewModel(questionRepo, quizId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

