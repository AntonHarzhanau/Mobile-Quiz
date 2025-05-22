package com.example.musicalquizz.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.model.AnswerDraft
import com.example.musicalquizz.data.model.QuestionDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateQuestionViewModel(
    private val parentVm: CreateQuizViewModel,
    handle: SavedStateHandle
) : AndroidViewModel(parentVm.getApplication<Application>()) {

    // NavArgs
    private val quizId         = handle.get<Long>("quizId")!!
    private val trackId        = handle.get<Long>("trackId")!!
    private val trackTitle     = handle.get<String>("trackTitle")!!
    private val trackArtist    = handle.get<String>("trackArtist")!!
    private val trackCoverUrl  = handle.get<String>("trackCoverUrl")!!
    private val trackPreviewUrl= handle.get<String>("trackPreviewUrl")!!

    // DAO for loading existing question
    private val questionDao = AppDatabase.getInstance(getApplication()).questionDao()

    // In-memory draft if already added in this session
    private val existingDraft: QuestionDraft? =
        parentVm.drafts.value?.firstOrNull { it.trackId == trackId }

    // Backing LiveData for the draft
    private val _draft = MutableLiveData<QuestionDraft>()
    val draft: LiveData<QuestionDraft> = _draft

    // UI fields
    val questionText = MutableLiveData<String>()
    val answers      = MutableLiveData<List<AnswerDraft>>()

    init {
        if (existingDraft != null) {
            // Use in-memory draft
            _draft.value     = existingDraft
            questionText.value = existingDraft.questionText
            answers.value      = existingDraft.answers.toList()
        } else {
            // Create a fresh draft, then attempt to load from DB
            val newDraft = QuestionDraft(
                trackId        = trackId,
                trackTitle     = trackTitle,
                trackArtist    = trackArtist,
                trackCoverUrl  = trackCoverUrl,
                trackPreviewUrl= trackPreviewUrl
            )
            _draft.value = newDraft
            questionText.value = newDraft.questionText
            answers.value      = newDraft.answers.toList()

            // Load existing saved question from DB, if any
            viewModelScope.launch(Dispatchers.IO) {
                val qwa = questionDao.getQuestionWithAnswersOnce(quizId, trackId)
                if (qwa != null) {
                    val loaded = QuestionDraft(
                        trackId        = qwa.question.trackId,
                        trackTitle     = qwa.question.trackTitle,
                        trackArtist    = qwa.question.trackArtist,
                        trackCoverUrl  = qwa.question.trackCoverUrl,
                        trackPreviewUrl= qwa.question.trackPreviewUrl,
                        questionText   = qwa.question.questionText,
                        answers        = qwa.answers.map { ans ->
                            AnswerDraft(
                                id         = ans.id,
                                questionId = ans.questionId,
                                answerText = ans.answerText,
                                isCorrect  = ans.isCorrect
                            )
                        }.toMutableList()
                    )
                    _draft.postValue(loaded)
                    questionText.postValue(loaded.questionText)
                    answers.postValue(loaded.answers.toList())
                }
            }
        }
    }

    fun addAnswer() {
        val d = _draft.value!!
        d.answers.add(AnswerDraft())
        answers.value = d.answers.toList()
    }

    fun updateAnswer(updated: AnswerDraft) {
        val d = _draft.value!!
        val idx = d.answers.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            d.answers[idx] = updated
            answers.value = d.answers.toList()
        }
    }

    fun saveDraft() {
        val d = _draft.value!!
        d.questionText = questionText.value.orEmpty()
        d.answers = answers.value!!.toMutableList()
        if (parentVm.trackHasQuestion(trackId)) {
            parentVm.updateDraft(d)
        } else {
            parentVm.addDraft(d)
        }
    }
}