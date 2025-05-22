package com.example.musicalquizz.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.data.db.repository.PlaylistRepository
import com.example.musicalquizz.data.db.repository.QuestionRepository
import com.example.musicalquizz.data.db.repository.QuizRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.repository.AnswerRepository
import com.example.musicalquizz.data.db.entities.QuestionEntity
import com.example.musicalquizz.data.model.Album
import com.example.musicalquizz.data.model.Artist
import com.example.musicalquizz.data.model.QuestionDraft
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.data.network.DeezerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class CreateQuizViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val db           = AppDatabase.getInstance(application)
    private val quizRepo     = QuizRepository(db.quizDao())
    private val questionRepo = QuestionRepository(db.questionDao())
    private val playlistRepo = PlaylistRepository(db, application)

    // SafeArgs-backed quizId
    private val initialQuizId = savedStateHandle.get<Long>("quizId") ?: 0L
    private val _quizId = MutableLiveData(initialQuizId)
    val quizId: LiveData<Long> = _quizId

    // playlistId must be set from Fragment
    private val _playlistId = MutableLiveData<Long>()
    fun setPlaylistId(id: Long) { _playlistId.value = id }

    // cover, title, description
    val coverUri    = MutableLiveData<Uri?>()
    val title       = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    // drafts
    private val _drafts = MutableLiveData<MutableList<QuestionDraft>>(mutableListOf())
    val drafts: LiveData<List<QuestionDraft>> = _drafts as LiveData<List<QuestionDraft>>

    fun findDraftForTrack(trackId: Long): QuestionDraft? =
        _drafts.value?.firstOrNull { it.trackId == trackId }

    fun trackHasQuestion(trackId: Long): Boolean =
        findDraftForTrack(trackId) != null

    fun addDraft(d: QuestionDraft) {
        _drafts.value!!.add(d)
        _drafts.value = _drafts.value
    }

    fun updateDraft(d: QuestionDraft) {
        val list = _drafts.value!!
        val idx  = list.indexOfFirst { it.trackId == d.trackId }
        if (idx >= 0) {
            list[idx] = d
            _drafts.value = list
        }
    }

    // playlistTracks depends on playlistId
    val playlistTracks: LiveData<List<Track>> = _playlistId.switchMap { pid ->
        playlistRepo.tracks(pid)
            .map { list ->
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
            .asLiveData()
    }

    fun init(loadedQuizId: Long) {
        if (_quizId.value == 0L && loadedQuizId != 0L) {
            _quizId.value = loadedQuizId
            savedStateHandle["quizId"] = loadedQuizId
        }
    }

    fun saveQuiz(onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = _quizId.value ?: 0L
            val entity = QuizEntity(
                id            = if (id != 0L) id else 0L,
                title         = title.value.orEmpty(),
                description   = description.value.orEmpty(),
                coverUri      = coverUri.value?.toString(),
                questionCount = _drafts.value!!.size
            )
            val newId = if (id == 0L) quizRepo.insert(entity)
            else { quizRepo.update(entity); id }
            _quizId.postValue(newId)
            savedStateHandle["quizId"] = newId
            onResult(newId)
        }
    }
}

