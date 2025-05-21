package com.example.musicalquizz.ui.fragments.quiz

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.QuizListAdapter
import com.example.musicalquizz.data.db.entities.PlaylistEntity
import com.example.musicalquizz.data.db.entities.QuizEntity
import com.example.musicalquizz.databinding.FragmentQuizListBinding
import com.example.musicalquizz.viewmodel.PlaylistViewModel
import com.example.musicalquizz.viewmodel.QuizListViewModel

class QuizListFragment : Fragment(R.layout.fragment_quiz_list) {

    private var _binding: FragmentQuizListBinding? = null
    private val binding get() = _binding!!

    private val quizVm     : QuizListViewModel by viewModels()
    private val playlistVm : PlaylistViewModel by activityViewModels()

    private var playlistsCache: List<PlaylistEntity> = emptyList()

    private val adapter by lazy {
        QuizListAdapter(
            onClick = { quiz: QuizEntity ->
                val action = QuizListFragmentDirections
                    .actionQuizListFragmentToQuizDetailFragment(
                        quizId = quiz.id,
                    )
                findNavController().navigate(action)
            },
            onLongClick = { quiz, anchor ->
                PopupMenu(requireContext(), anchor).apply {
                    menuInflater.inflate(R.menu.menu_quiz_item, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_edit -> {
                                //TODO: navigate to edit quiz
                                true
                            }
                            R.id.action_delete -> {
                                quizVm.deleteQuiz(quiz)
                                Toast.makeText(
                                    requireContext(),
                                    "Quiz \"${quiz.title}\" deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentQuizListBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistVm.playlists.observe(viewLifecycleOwner) { list ->
            playlistsCache = list
        }

        binding.rvQuizzes.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvQuizzes.adapter = adapter
        quizVm.quizzes.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }


        binding.fabAddQuiz.setOnClickListener {
            if (playlistsCache.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("First create a playlist")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                val titles = playlistsCache.map { it.title }.toTypedArray()
                AlertDialog.Builder(requireContext())
                    .setTitle("Select a playlist for the quiz")
                    .setItems(titles) { _, which ->
                        val sel = playlistsCache[which]
                        val action = QuizListFragmentDirections
                            .actionQuizListFragmentToCreateQuizFragment(
                                quizId  = 0L,
                                albumId = sel.id
                            )
                        findNavController().navigate(action)
                    }
                    .show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
