package com.example.musicalquizz.ui.fragments.quiz

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.musicalquizz.databinding.FragmentCreateQuestionBinding
import com.example.musicalquizz.viewmodel.CreateQuestionViewModel
import com.example.musicalquizz.viewmodel.CreateQuestionViewModelFactory
import com.example.musicalquizz.viewmodel.CreateQuizViewModel
import androidx.core.widget.doAfterTextChanged

class CreateQuestionFragment : Fragment(R.layout.fragment_create_question) {
    private var _b: FragmentCreateQuestionBinding? = null
    private val b get() = _b!!

    private val parentVm: CreateQuizViewModel by activityViewModels()
    private val args: CreateQuestionFragmentArgs by navArgs()
    private val vm: CreateQuestionViewModel by viewModels {
        CreateQuestionViewModelFactory(parentVm, this, arguments)
    }

    private lateinit var adapter: QuizAnswerAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentCreateQuestionBinding.inflate(inflater, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.questionText.observe(viewLifecycleOwner) { text ->
            if (b.etQuestionText.text.toString() != text)
                b.etQuestionText.setText(text)
        }

        b.etQuestionText.doAfterTextChanged { editable ->
            vm.questionText.value = editable.toString()
        }

        adapter = QuizAnswerAdapter { updated -> vm.updateAnswer(updated) }
        b.rvAnswers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CreateQuestionFragment.adapter
        }
        vm.answers.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        b.btnAddAnswer.setOnClickListener {
            vm.addAnswer()
        }

        vm.draft.observe(viewLifecycleOwner) { draft ->
            b.tvQuestionTrackTitle.text = draft.trackTitle
            Glide.with(this)
                .load(draft.trackCoverUrl)
                .placeholder(R.color.gray_light)
                .into(b.imgQuestionTrackCover)
        }

        b.btnPlayPreview.setOnClickListener {
            vm.togglePlay(requireContext())
        }

        vm.isPlaying.observe(viewLifecycleOwner, Observer { playing ->
            b.btnPlayPreview.text = if (playing) "Pause" else "Play"
        })

        b.btnSaveQuestion.setOnClickListener {
            vm.saveDraft()
            findNavController().popBackStack()
        }

        b.btnCancelQuestion.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        mediaPlayer?.release()
        _b = null
        super.onDestroyView()
    }
}
