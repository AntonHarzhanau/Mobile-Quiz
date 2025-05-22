package com.example.musicalquizz.viewmodel

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.entities.QuestionWithAnswers
import com.example.musicalquizz.data.db.repository.QuestionRepository
import com.example.musicalquizz.data.network.DeezerApi
import kotlinx.coroutines.launch


class QuizGameViewModel(
    application: Application,
    private val questionRepo: QuestionRepository,
    private val quizId: Long,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

    private val qaList: LiveData<List<QuestionWithAnswers>> =
        questionRepo.getQuestionsForQuiz(quizId)


    private val _currentIndex = state.getLiveData("currentIndex", 0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _selectedAnswers = state.getLiveData("selectedAnswers", emptySet<Long>())
    val selectedAnswers: LiveData<Set<Long>> = _selectedAnswers


    private val _isSubmitted = state.getLiveData("isSubmitted", false)
    val isSubmitted: LiveData<Boolean> = _isSubmitted


    private var score = state.get<Int>("score") ?: 0
    val correctCount: Int get() = score
    val totalCount: Int   get() = qaList.value?.size ?: 0

    val currentQA: LiveData<QuestionWithAnswers?> = MediatorLiveData<QuestionWithAnswers?>().apply {
        fun refresh() {
            val list = qaList.value
            val idx  = _currentIndex.value ?: 0
            value = list?.getOrNull(idx)
        }
        addSource(qaList)        { refresh() }
        addSource(_currentIndex) { refresh() }
    }

    val currentAnswers: LiveData<List<AnswerEntity>> =
        currentQA.map { it?.answers.orEmpty() }


    private val app = getApplication<Application>()
    private var currentTrackId: Long? = null

    val player: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun togglePlay() {
        val qa = currentQA.value ?: return
        val tid = qa.question.trackId

        when {
            player.isPlaying -> {
                player.pause()
                _isPlaying.value = false
            }
            currentTrackId == tid && player.currentPosition > 0 -> {
                player.start()
                _isPlaying.value = true
            }
            else -> {
                viewModelScope.launch {
                    try {
                        val url = DeezerApi.retrofitService.getTrackById(tid).preview
                        url.let {
                            currentTrackId = tid
                            player.reset()
                            player.setDataSource(app, it.toUri())
                            player.setOnPreparedListener {
                                it.start()
                                _isPlaying.value = true
                            }
                            player.setOnCompletionListener {
                                it.seekTo(0)
                                _isPlaying.value = false
                            }
                            player.prepareAsync()
                        }
                    } catch (_: Exception) { /* ignore */ }
                }
            }
        }
    }


    fun resetPlayer() {
        if (player.isPlaying) player.pause()
        player.seekTo(0)
        _isPlaying.value = false
        currentTrackId = null
    }


    fun toggleAnswer(answerId: Long, checked: Boolean) {
        if (_isSubmitted.value == true) return
        val answers   = currentQA.value?.answers.orEmpty()
        val multiMode = answers.count { it.isCorrect } > 1
        val updated = if (!multiMode && checked) {
            setOf(answerId)
        } else {
            _selectedAnswers.value!!.toMutableSet().apply {
                if (checked) add(answerId) else remove(answerId)
            }
        }
        _selectedAnswers.value = updated
        state["selectedAnswers"] = updated
    }

    /** Submit */
    fun submit(): Boolean {
        val qa = currentQA.value ?: return false
        val correctIds = qa.answers.filter { it.isCorrect }.map { it.id }.toSet()
        val ok = correctIds == _selectedAnswers.value
        if (ok) {
            score++
            state["score"] = score
        }
        _isSubmitted.value = true
        state["isSubmitted"] = true
        return ok
    }

    /** Skip / Next */

    fun skip(): Boolean {

        _selectedAnswers.value = emptySet()
        state.set<Set<Long>>("selectedAnswers", emptySet())

        _isSubmitted.value = false
        state["isSubmitted"] = false

        val nextIdx = (_currentIndex.value ?: 0) + 1
        return if (qaList.value != null && nextIdx < qaList.value!!.size) {
            _currentIndex.value = nextIdx
            state["currentIndex"] = nextIdx
            true
        } else {
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}

class QuizGameViewModelFactory(
    private val application: Application,
    private val questionRepo: QuestionRepository,
    private val quizId: Long,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String, modelClass: Class<T>, handle: SavedStateHandle
    ): T = QuizGameViewModel(application, questionRepo, quizId, handle) as T
}