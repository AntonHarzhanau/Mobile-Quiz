package com.example.musicalquizz.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalquizz.data.model.AnswerDraft
import com.example.musicalquizz.data.model.QuestionDraft
import com.example.musicalquizz.data.network.DeezerApi
import kotlinx.coroutines.launch


class CreateQuestionViewModel(
    private val parentVm: CreateQuizViewModel,
    handle: SavedStateHandle
) : ViewModel() {

    private val trackId         = handle.get<Long>("trackId")!!
    private val trackTitle      = handle.get<String>("trackTitle")!!
    private val trackArtist     = handle.get<String>("trackArtist")!!
    private val trackCoverUrl   = handle.get<String>("trackCoverUrl")!!
    private val trackPreviewUrl = handle.get<String>("trackPreviewUrl")!!

    private val existingDraft = parentVm.drafts.value
        ?.firstOrNull { it.trackId == trackId }

    private val _draft = MutableLiveData<QuestionDraft>().apply {
        value = existingDraft ?: QuestionDraft(
            trackId        = trackId,
            trackTitle     = trackTitle,
            trackArtist    = trackArtist,
            trackCoverUrl  = trackCoverUrl,
            trackPreviewUrl= trackPreviewUrl
        )
    }

    val draft: LiveData<QuestionDraft> = _draft

    val questionText = MutableLiveData<String>().apply {
        value = existingDraft?.questionText.orEmpty()
    }

    private val _answers = MutableLiveData<List<AnswerDraft>>().apply {
        value = existingDraft?.answers?.toList() ?: emptyList()
    }

    val answers: LiveData<List<AnswerDraft>> = _answers

    fun addAnswer() {
        val list = _answers.value!!.toMutableList()
        list.add(AnswerDraft())
        _answers.value = list
    }

    fun updateAnswer(updated: AnswerDraft) {
        val list = _answers.value!!.toMutableList()
        val idx = list.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            list[idx] = updated
            _answers.value = list
        }
    }


    fun saveDraft() {
        val d = QuestionDraft(
            trackId        = trackId,
            trackTitle     = trackTitle,
            trackArtist    = trackArtist,
            trackCoverUrl  = trackCoverUrl,
            trackPreviewUrl= trackPreviewUrl,
            questionText   = questionText.value.orEmpty(),
            answers        = _answers.value!!.toMutableList()
        )
        if (parentVm.trackHasQuestion(trackId)) parentVm.updateDraft(d)
        else parentVm.addDraft(d)
    }


    private var currentTrackId: Long? = null
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    var player: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    fun togglePlay(context: Context) {
        when {
            player.isPlaying -> {
                player.pause()
                _isPlaying.value = false
            }
            currentTrackId == trackId && player.currentPosition > 0 -> {
                player.start()
                _isPlaying.value = true
            }
            else -> {
                viewModelScope.launch {
                    try {
                        val url = DeezerApi.retrofitService.getTrackById(trackId).preview
                        url.let {
                            currentTrackId = trackId
                            player.reset()
                            player.setDataSource(context, it.toUri())
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

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}