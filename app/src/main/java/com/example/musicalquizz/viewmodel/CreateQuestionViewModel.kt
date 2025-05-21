package com.example.musicalquizz.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.musicalquizz.data.model.AnswerDraft
import com.example.musicalquizz.data.model.QuestionDraft

class CreateQuestionViewModel(
    private val parentVm: CreateQuizViewModel,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    // NavArgs
    private val trackId         = savedStateHandle.get<Long>("trackId")!!
    private val trackTitle      = savedStateHandle.get<String>("trackTitle")!!
    private val trackArtist     = savedStateHandle.get<String>("trackArtist")!!
    private val trackCoverUrl   = savedStateHandle.get<String>("trackCoverUrl")!!
    private val trackPreviewUrl = savedStateHandle.get<String>("trackPreviewUrl")!!

    private val _draft = MutableLiveData<QuestionDraft>().apply {
        value = QuestionDraft(trackId, trackTitle, trackArtist, trackCoverUrl, trackPreviewUrl)
    }
    val draft: LiveData<QuestionDraft> = _draft

    // UI
    val questionText = MutableLiveData<String>("")
    val answers      = MutableLiveData<List<AnswerDraft>>(emptyList())

    init {
        parentVm.findDraftForTrack(trackId)?.also { existing ->
            _draft.value      = existing
            questionText.value = existing.questionText
            answers.value      = existing.answers
        }
    }

    fun addEmptyAnswer() {
        val list = answers.value!!.toMutableList()
        list += AnswerDraft()
        answers.value = list
    }

    fun updateAnswer(updated: AnswerDraft) {
        val list = answers.value!!.toMutableList()
        val idx = list.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            list[idx] = updated
            answers.value = list
        }
    }

    fun saveDraft() {
        val filled = _draft.value!!.copy(
            questionText = questionText.value.orEmpty(),
            answers      = answers.value!!.toMutableList()
        )
        if (parentVm.trackHasQuestion(trackId)) parentVm.updateDraft(filled)
        else                            parentVm.addDraft(filled)
    }

    fun playPreview() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(trackPreviewUrl)
            setOnPreparedListener { it.start() }
            prepareAsync()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
