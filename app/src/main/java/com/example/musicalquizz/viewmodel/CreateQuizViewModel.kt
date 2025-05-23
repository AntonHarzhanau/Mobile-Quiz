package com.example.musicalquizz.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.data.db.repository.PlaylistRepository
import com.example.musicalquizz.data.db.repository.QuizRepository
import com.example.musicalquizz.data.model.Album
import com.example.musicalquizz.data.model.Artist
import kotlinx.coroutines.launch
import com.example.musicalquizz.data.model.QuestionDraft
import com.example.musicalquizz.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateQuizViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repo = QuizRepository(
        application.applicationContext
    )
    private val playlistRepo = PlaylistRepository(
        AppDatabase.getInstance(application),
        application.applicationContext
    )

    val title       = MutableLiveData<String>("")
    val description = MutableLiveData<String>("")
    val coverUri    = MutableLiveData<Uri?>(null)

    private val _playlistId = MutableLiveData<Long>()
    fun setPlaylistId(id: Long) { _playlistId.value = id }

    val playlistTracks: LiveData<List<Track>> = _playlistId.switchMap { pid ->
        // 1) Flow → LiveData
        val live = playlistRepo.tracks(pid).asLiveData()
        // 2) LiveData<List<PlaylistTrackEntity>> → LiveData<List<Track>>
        live.map { list ->
            list.map { e ->
                Track(
                    id       = e.trackId,
                    title    = e.title,
                    artist   = Artist(e.artistName),
                    album    = Album(
                        id     = 0L,
                        title  = "",
                        artist = Artist(e.artistName),
                        cover  = e.albumCoverUrl
                    ),
                    duration = "",
                    preview  = e.previewUrl
                )
            }
        }
    }

    private val _drafts = MutableLiveData<MutableList<QuestionDraft>>(mutableListOf())
    val drafts: LiveData<List<QuestionDraft>> =
        _drafts.map { it.toList() }

    private val _saveDone = MutableLiveData<Boolean>()
    val saveDone: LiveData<Boolean> = _saveDone


    fun addDraft(draft: QuestionDraft) {
        _drafts.value?.apply {
            add(draft)
            _drafts.value = this
        }
    }

    fun updateDraft(draft: QuestionDraft) {
        _drafts.value?.apply {
            val idx = indexOfFirst { it.trackId == draft.trackId }
            if (idx >= 0) {
                set(idx, draft)
                _drafts.value = this
            }
        }
    }
    fun clearSaveDone() {
        _saveDone.value = false
    }
    fun clearDrafts() {
        _drafts.value = mutableListOf()
    }
    fun clearCover() {
        coverUri.value = null
    }

    fun trackHasQuestion(trackId: Long): Boolean =
        _drafts.value?.any { it.trackId == trackId } == true

    fun saveQuiz() {
        val quizEntity = QuizEntity(
            id            = 0L,
            title         = title.value.orEmpty(),
            description   = description.value.orEmpty(),
            coverUri      = coverUri.value?.toString(),
            questionCount = _drafts.value?.size ?: 0
        )
        val toSave = _drafts.value?.toList().orEmpty()

        viewModelScope.launch(Dispatchers.IO) {
            repo.saveQuizWithQuestions(quizEntity, toSave)
            withContext(Dispatchers.Main) {
                _saveDone.value = true
            }
        }
    }
}


