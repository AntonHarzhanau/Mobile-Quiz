package com.example.musicalquizz.ui.fragments.quiz

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.QuizTrackAdapter
import com.example.musicalquizz.databinding.FragmentCreateQuizBinding
import com.example.musicalquizz.viewmodel.CreateQuizViewModel

class CreateQuizFragment : Fragment(R.layout.fragment_create_quiz) {

    private var _binding: FragmentCreateQuizBinding? = null
    private val binding get() = _binding!!

    private val args: CreateQuizFragmentArgs by navArgs()

    private val createQuizEntry by lazy {
        findNavController().getBackStackEntry(R.id.createQuizFragment)
    }


    private val viewModel: CreateQuizViewModel by viewModels({ createQuizEntry }) {
        defaultViewModelProviderFactory
    }


    private val tracksAdapter = QuizTrackAdapter(
        onClick = { track ->
            val draft = viewModel.findDraftForTrack(track.id)

            val quizId      = args.quizId
            val trackId     = track.id
            val questionId  = draft?.trackId ?: 0L
            val title       = track.title
            val artist      = track.artist.name
            val coverUrl    = track.album.cover
            val previewUrl  = track.preview


            val action = CreateQuizFragmentDirections
                .actionCreateQuizFragmentToCreateQuestionFragment(
                    quizId         = quizId,
                    trackId        = trackId,
                    questionId     = questionId,
                    trackTitle     = title,
                    trackArtist    = artist,
                    trackCoverUrl  = coverUrl,
                    trackPreviewUrl= previewUrl,
                )
            findNavController().navigate(action)
        },
        onLongClick = { _, _ -> /* … */ },
        hasQuestion = { viewModel.trackHasQuestion(it) }
    )

    private val pickImage = registerForActivityResult(GetContent()) { uri: Uri? ->
        viewModel.setCoverUri(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateQuizBinding.bind(view)

        // Cover / Title / Desc
        binding.btnSelectImage.setOnClickListener { pickImage.launch("image/*") }
        viewModel.coverUri.observe(viewLifecycleOwner) { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.color.gray_light)
                .into(binding.imgQuizPlaceholder)
        }
        viewModel.title.observe(viewLifecycleOwner) {
            binding.etQuizTitle.setText(it)
        }
        viewModel.description.observe(viewLifecycleOwner) {
            binding.etQuizDescription.setText(it)
        }

        binding.rvAlbumTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tracksAdapter
        }

        viewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            tracksAdapter.submitList(tracks)
        }

        // Save / Cancel
        binding.btnSaveQuiz.setOnClickListener {
            viewModel.title.value       = binding.etQuizTitle.text.toString()
            viewModel.description.value = binding.etQuizDescription.text.toString()
            viewModel.saveAll()
            findNavController().navigate(
                CreateQuizFragmentDirections
                    .actionCreateQuizFragmentToQuizListFragment()
            )
        }
        binding.btnCancelQuiz.setOnClickListener {
            findNavController().navigate(
                CreateQuizFragmentDirections
                    .actionCreateQuizFragmentToQuizListFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

