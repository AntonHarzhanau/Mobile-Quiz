package com.example.musicalquizz.ui.fragments.quiz

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.QuizTrackAdapter
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.databinding.FragmentCreateQuizBinding
import com.example.musicalquizz.viewmodel.CreateQuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CreateQuizFragment : Fragment(R.layout.fragment_create_quiz) {

    private var _binding: FragmentCreateQuizBinding? = null
    private val binding get() = _binding!!
    private val questionDao by lazy { AppDatabase.getInstance(requireContext()).questionDao() }
    private val args: CreateQuizFragmentArgs by navArgs()
    private val vm: CreateQuizViewModel by viewModels()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vm.coverUri.value = uri
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCreateQuizBinding.bind(view)


        vm.init(args.quizId)
        vm.setPlaylistId(args.albumId)

        binding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        vm.coverUri.observe(viewLifecycleOwner) { uri: Uri? ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.color.gray_light)
                .into(binding.imgQuizPlaceholder)
        }

        val adapter = QuizTrackAdapter(
            onClick = { track ->

                vm.title.value       = binding.etQuizTitle.text.toString()
                vm.description.value = binding.etQuizDescription.text.toString()

                vm.saveQuiz { quizId ->
                    findNavController().navigate(
                        CreateQuizFragmentDirections
                            .actionCreateQuizFragmentToCreateQuestionFragment(
                                quizId         = quizId,
                                trackId        = track.id,
                                trackTitle     = track.title,
                                trackArtist    = track.artist.name,
                                trackCoverUrl  = track.album.cover,
                                trackPreviewUrl= track.preview
                            )
                    )
                }
            },
            onLongClick = { _, _ -> },
            hasQuestion = { trackId ->

                val qid = vm.quizId.value ?: return@QuizTrackAdapter false
                runBlocking(Dispatchers.IO) {
                    questionDao.countQuestionsForQuizTrack(qid, trackId) > 0
                }
            }
        )
        binding.rvAlbumTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlbumTracks.adapter    = adapter


        vm.playlistTracks.observe(viewLifecycleOwner) { tracks: List<Track> ->
            adapter.submitList(tracks)
        }


        binding.btnSaveQuiz.setOnClickListener {

            vm.title.value       = binding.etQuizTitle.text.toString()
            vm.description.value = binding.etQuizDescription.text.toString()

            vm.saveQuiz {

                findNavController().navigate(
                    CreateQuizFragmentDirections
                        .actionCreateQuizFragmentToQuizListFragment()
                )
            }
        }

        binding.btnCancelQuiz.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
