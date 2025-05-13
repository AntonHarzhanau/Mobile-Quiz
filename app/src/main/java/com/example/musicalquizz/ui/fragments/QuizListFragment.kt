package com.example.musicalquizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.QuizAdapter
import com.example.musicalquizz.databinding.FragmentQuizListBinding
import com.example.musicalquizz.db.entities.QuizEntity
import com.example.musicalquizz.viewmodel.QuizViewModel
import kotlin.getValue

class QuizListFragment : Fragment() {
    private var _binding: FragmentQuizListBinding? = null
    private val binding get() = _binding!!
    private val vm: QuizViewModel by viewModels() // или activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentQuizListBinding.inflate(inflater, c, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Поддержка GridLayoutManager spanCount = 2
        binding.rvQuizzes.layoutManager = GridLayoutManager(requireContext(), 2)

        vm.quizzes.observe(viewLifecycleOwner) { quizzes ->
            binding.rvQuizzes.adapter = QuizAdapter(quizzes,
                onClick = { quiz ->
                    // навигация к деталям
                    val action = QuizListFragmentDirections
                        .actionQuizListFragmentToQuizDetailFragment(quiz.id)
                    findNavController().navigate(action)
                },
                onLongClick = { view, quiz ->
                    showPopupMenu(view, quiz)
                }
            )
        }

        binding.fabAddQuiz.setOnClickListener {
            // открыть фрагмент/диалог создания квиза
            findNavController().navigate(R.id.createQuizFragment)
        }
    }

    private fun showPopupMenu(anchor: View, quiz: QuizEntity) {
        PopupMenu(requireContext(), anchor).apply {
            inflate(R.menu.menu_quiz_item) // с пунктом удалить
            setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.action_delete_quiz) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Удалить квиз?")
                        .setMessage("Вы уверены, что хотите удалить «${quiz.title}»?")
                        .setPositiveButton("Да") { _, _ ->
                            vm.deleteQuiz(quiz)
                        }
                        .setNegativeButton("Отмена", null)
                        .show()
                    true
                } else false
            }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
