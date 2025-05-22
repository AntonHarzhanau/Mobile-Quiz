package com.example.musicalquizz.ui.fragments.quiz

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.QuizAnswerAdapter
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.entities.QuestionEntity
import com.example.musicalquizz.data.model.QuestionDraft
import com.example.musicalquizz.databinding.FragmentCreateQuestionBinding
import com.example.musicalquizz.viewmodel.CreateQuestionViewModel
import com.example.musicalquizz.viewmodel.CreateQuestionViewModelFactory
import com.example.musicalquizz.viewmodel.CreateQuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext


class CreateQuestionFragment : Fragment(R.layout.fragment_create_question) {

    private var _binding: FragmentCreateQuestionBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel that holds all drafts
    private val parentVm: CreateQuizViewModel by activityViewModels()
    private val args: CreateQuestionFragmentArgs by navArgs()
    private val viewModel: CreateQuestionViewModel by viewModels {
        CreateQuestionViewModelFactory(parentVm, this, arguments)
    }

    private lateinit var adapter: QuizAnswerAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var currentDraft: QuestionDraft? = null

    private val questionDao by lazy { AppDatabase.getInstance(requireContext()).questionDao() }
    private val answerDao   by lazy { AppDatabase.getInstance(requireContext()).answerDao() }
    private val quizDao     by lazy { AppDatabase.getInstance(requireContext()).quizDao() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCreateQuestionBinding.bind(view)

        // Observe the draft to initialize UI
        viewModel.draft.observe(viewLifecycleOwner, Observer { draft ->
            currentDraft = draft
            binding.tvQuestionTrackTitle.text = draft.trackTitle
            binding.etQuestionText.setText(draft.questionText)
            Glide.with(this)
                .load(draft.trackCoverUrl)
                .placeholder(R.color.gray_light)
                .into(binding.imgQuestionTrackCover)
        })

        // Set up media preview
        binding.btnPlayPreview.setOnClickListener {
            currentDraft?.trackPreviewUrl?.let { url ->
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(requireContext(), url.toUri())
                    setOnPreparedListener { it.start() }
                    prepareAsync()
                }
            }
        }

        // RecyclerView + Adapter
        adapter = QuizAnswerAdapter { updated ->
            viewModel.updateAnswer(updated)
        }
        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CreateQuestionFragment.adapter
        }

        // When answers list changes, submit to adapter
        viewModel.answers.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

        // Add Answer button
        binding.btnAddAnswer.setOnClickListener {
            viewModel.addAnswer()
        }

        // Save Question button
        binding.btnSaveQuestion.setOnClickListener {
            val questionText = binding.etQuestionText.text.toString().trim()
            val drafts = viewModel.answers.value ?: emptyList()
            if (questionText.isEmpty() || drafts.isEmpty()) return@setOnClickListener


            lifecycleScope.launch(Dispatchers.IO) {

                val qEnt = QuestionEntity(
                    id              = 0L,
                    quizId          = args.quizId,
                    trackId         = args.trackId,
                    trackTitle      = args.trackTitle,
                    trackArtist     = args.trackArtist,
                    trackCoverUrl   = args.trackCoverUrl,
                    trackPreviewUrl = args.trackPreviewUrl,
                    questionText    = questionText
                )
                val qId = questionDao.insertQuestion(qEnt)


                val ansEnts = drafts.map { d ->
                    AnswerEntity(
                        id         = 0L,
                        questionId = qId,
                        answerText = d.answerText,
                        isCorrect  = d.isCorrect
                    )
                }
                answerDao.insertAnswers(ansEnts)

                val newCount = questionDao.countQuestionsForQuiz(args.quizId)
                quizDao.updateQuestionCount(args.quizId, newCount)

                withContext(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
        }

        // Cancel
        binding.btnCancelQuestion.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        mediaPlayer?.release()
        _binding = null
        super.onDestroyView()
    }
}


