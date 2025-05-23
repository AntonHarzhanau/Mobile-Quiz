package com.example.musicalquizz.ui.fragments.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.QuizTrackAdapter
import com.example.musicalquizz.databinding.FragmentCreateQuizBinding
import com.example.musicalquizz.viewmodel.CreateQuizViewModel

class CreateQuizFragment : Fragment(R.layout.fragment_create_quiz) {

    private var _b: FragmentCreateQuizBinding? = null
    private val b get() = _b!!

    private val args: CreateQuizFragmentArgs by navArgs()
    private val vm: CreateQuizViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> vm.coverUri.value = uri }

    private val quizTrackAdapter by lazy {
        QuizTrackAdapter(
            onClick = { track ->
                val action = CreateQuizFragmentDirections
                    .actionCreateQuizFragmentToCreateQuestionFragment(
                        quizId         = 0L,
                        trackId        = track.id,
                        trackTitle     = track.title,
                        trackArtist    = track.artist.name,
                        trackCoverUrl  = track.album.cover,
                        trackPreviewUrl= track.preview
                    )
                findNavController().navigate(action)
            },
            onLongClick = { _, _ -> }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCreateQuizBinding.inflate(inflater, container, false)
            .also { _b = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.setPlaylistId(args.albumId)

        b.rvAlbumTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quizTrackAdapter
        }

        vm.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            quizTrackAdapter.submitList(tracks)
        }

        vm.drafts.observe(viewLifecycleOwner) { drafts ->
            val idsWithQuestions = drafts.map { it.trackId }.toSet()
            quizTrackAdapter.setHasQuestions(idsWithQuestions)
        }

        b.btnSelectImage.setOnClickListener { pickImage.launch("image/*") }
        vm.coverUri.observe(viewLifecycleOwner) { uri ->
            Glide.with(this).load(uri).placeholder(R.color.gray_light)
                .into(b.imgQuizPlaceholder)
        }
        vm.drafts.observe(viewLifecycleOwner) { drafts ->
            quizTrackAdapter.setHasQuestions(drafts.map { it.trackId }.toSet())
        }
        b.btnSaveQuiz.setOnClickListener {
            vm.title.value       = b.etQuizTitle.text.toString().trim()
            vm.description.value = b.etQuizDescription.text.toString().trim()
            vm.saveQuiz()
        }
        vm.saveDone.observe(viewLifecycleOwner) { done ->
            if (done) {
                if (args.quizId == 0L) {
                    vm.clearDrafts()
                    vm.clearCover()
                }
                findNavController().popBackStack()
                vm.clearSaveDone()
            }
        }
        b.btnCancelQuiz.setOnClickListener {

            if (args.quizId == 0L) {
                vm.clearDrafts()
                vm.clearCover()
            }
            findNavController().popBackStack()
            vm.clearSaveDone()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

