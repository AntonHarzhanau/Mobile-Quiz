package com.example.musicalquizz.ui.fragments.quiz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

class CreateQuestionFragment : Fragment(R.layout.fragment_create_question) {

    private var _binding: FragmentCreateQuestionBinding? = null
    private val binding get() = _binding!!

    private val args: CreateQuestionFragmentArgs by navArgs()

    // take parent-ViewModel from nested NavGraph with id createQuizFlow
    private val parentEntry by lazy {
        findNavController().getBackStackEntry(R.id.createQuizFragment)
    }
    private val parentVm: CreateQuizViewModel by viewModels({ parentEntry }) {
        defaultViewModelProviderFactory
    }
    // we create our VM through the factory
    private val viewModel: CreateQuestionViewModel by viewModels {
        CreateQuestionViewModelFactory(
            parentVm    = parentVm,
            owner       = this,
            defaultArgs = arguments
        )
    }

    private lateinit var answersAdapter: QuizAnswerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCreateQuestionBinding.bind(view)

        // 1) track info
        binding.tvQuestionTrackTitle.text = args.trackTitle
        Glide.with(this)
            .load(args.trackCoverUrl)
            .placeholder(R.color.gray_light)
            .into(binding.imgQuestionTrackCover)
        binding.btnPlayPreview.setOnClickListener {
            viewModel.playPreview()
        }

        // 2) RecyclerView for answers
        answersAdapter = QuizAnswerAdapter { updatedDraft ->
            viewModel.updateAnswer(updatedDraft)
        }
        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answersAdapter
        }
        viewModel.answers.observe(viewLifecycleOwner) { list ->
            answersAdapter.submitList(list)
        }
        binding.btnAddAnswer.setOnClickListener {
            viewModel.addEmptyAnswer()
        }

        // 3) Save / Cancel
        binding.btnSaveQuestion.setOnClickListener {
            viewModel.saveDraft()
            findNavController().popBackStack()
        }
        binding.btnCancelQuestion.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


