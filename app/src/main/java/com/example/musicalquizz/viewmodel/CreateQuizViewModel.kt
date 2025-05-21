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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class CreateQuizViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {

    // SafeArgs
    private val quizId     = savedStateHandle.get<Long>("quizId") ?: 0L
    private val playlistId = savedStateHandle.get<Long>("albumId") ?: 0L

    private val db         = AppDatabase.getInstance(app)
    private val quizRepo   = QuizRepository(db.quizDao())
    private val questionRepo = QuestionRepository(db.questionDao())
    private val answerRepo   = AnswerRepository(db.answerDao())
    private val playlistRepo = PlaylistRepository(db, app)

    // UI-champs
    val coverUri    = MutableLiveData<Uri?>()
    val title       = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    // ——————————————————————
    // Question drafts in memory
    private val _drafts = MutableLiveData<MutableList<QuestionDraft>>(mutableListOf())
    val drafts: LiveData<List<QuestionDraft>> = _drafts as LiveData<List<QuestionDraft>>

    fun findDraftForTrack(trackId: Long): QuestionDraft? =
        _drafts.value?.firstOrNull { it.trackId == trackId }

    fun trackHasQuestion(trackId: Long): Boolean =
        _drafts.value?.any { it.trackId == trackId } == true

    fun addDraft(draft: QuestionDraft) {
        _drafts.value!!.add(draft)
        _drafts.notifyObserver()
    }

    fun updateDraft(updated: QuestionDraft) {
        val list = _drafts.value!!
        val idx = list.indexOfFirst { it.trackId == updated.trackId }
        if (idx >= 0) {
            list[idx] = updated
            _drafts.notifyObserver()
        }
    }

    fun removeDraft(trackId: Long) {
        _drafts.value!!.removeAll { it.trackId == trackId }
        _drafts.notifyObserver()
    }
    // ——————————————————————

    // Tracks from playlist
    val playlistTracks: LiveData<List<Track>> =
        playlistRepo.tracks(playlistId)
            .map { entities ->
                entities.map { e ->
                    Track(
                        id       = e.trackId,
                        title    = e.title,
                        artist   = Artist(name = e.artistName),
                        album    = Album(
                            id     = 0L,
                            title  = "",
                            artist = Artist(name = e.artistName),
                            cover  = e.albumCoverUrl
                        ),
                        duration = "",
                        preview  = e.previewUrl
                    )
                }
            }
            .asLiveData()

    fun setCoverUri(uri: Uri?) { coverUri.value = uri }

    /** Save quiz + all drafts to DB */
    fun saveAll() = viewModelScope.launch(Dispatchers.IO) {

        val quizEntity = QuizEntity(
            id            = if (quizId != 0L) quizId else 0L,
            title         = title.value.orEmpty(),
            description   = description.value.orEmpty(),
            coverUri      = coverUri.value?.toString(),
            questionCount = _drafts.value!!.size
        )

        val newQuizId = if (quizEntity.id == 0L) {
            quizRepo.insert(quizEntity)
        } else {
            quizRepo.update(quizEntity)
            quizEntity.id
        }

        _drafts.value!!.forEach { draft ->
            val qEnt = QuestionEntity(
                id              = 0L,
                quizId          = newQuizId,
                trackId         = draft.trackId,
                trackTitle      = draft.trackTitle,
                trackArtist     = draft.trackArtist,
                trackCoverUrl   = draft.trackCoverUrl,
                trackPreviewUrl = draft.trackPreviewUrl,
                questionText    = draft.questionText
            )
            val qId = questionRepo.insert(qEnt)
            val ansEnts = draft.answers.map { ad ->
                AnswerEntity(
                    id         = 0L,
                    questionId = qId,
                    answerText = ad.answerText,
                    isCorrect  = ad.isCorrect
                )
            }
            answerRepo.insertAll(ansEnts)
        }
    }
}

// extension LiveData<List<…>>
private fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}