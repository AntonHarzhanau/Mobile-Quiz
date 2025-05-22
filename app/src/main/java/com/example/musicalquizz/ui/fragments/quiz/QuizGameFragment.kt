package com.example.musicalquizz.ui.fragments.quiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquizz.adapter.AnswerAdapter
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.data.db.repository.QuestionRepository
import com.example.musicalquizz.viewmodel.QuizGameViewModel
import com.example.musicalquizz.viewmodel.QuizGameViewModelFactory
import com.example.musicalquizz.R

class QuizGameFragment : Fragment(R.layout.fragment_quiz_game) {

    private val args by navArgs<QuizGameFragmentArgs>()
    private val vm: QuizGameViewModel by viewModels {
        QuizGameViewModelFactory(
            application   = requireActivity().application,
            questionRepo  = QuestionRepository(
                AppDatabase.getInstance(requireContext()).questionDao()
            ),
            quizId        = args.quizId,
            owner         = this,
            defaultArgs   = arguments
        )
    }

    private lateinit var exitBtn: ImageButton
    private lateinit var playBtn: ImageButton
    private lateinit var questionTv: TextView
    private lateinit var rvAnswers: RecyclerView
    private lateinit var skipNextBtn: Button
    private lateinit var submitBtn: Button
    private lateinit var adapter: AnswerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitBtn     = view.findViewById(R.id.btn_exit)
        playBtn     = view.findViewById(R.id.btn_play_preview)
        questionTv  = view.findViewById(R.id.tv_question)
        rvAnswers   = view.findViewById(R.id.rv_answers)
        skipNextBtn = view.findViewById(R.id.btn_skip_next)
        submitBtn   = view.findViewById(R.id.btn_submit)

        exitBtn.setOnClickListener { findNavController().popBackStack() }

        // RecyclerView + Adapter
        rvAnswers.layoutManager = LinearLayoutManager(requireContext())
        adapter = AnswerAdapter(isMulti = false) { id, checked ->
            vm.toggleAnswer(id, checked)
        }
        rvAnswers.adapter = adapter


        vm.isSubmitted.observe(viewLifecycleOwner) { submitted ->
            if (submitted) {
                adapter.markSubmitted()
                submitBtn.isEnabled = false
                skipNextBtn.text    = getString(R.string.next)
            } else {
                skipNextBtn.text    = getString(R.string.skip)
            }
        }

        // Submit – enabled по selectedAnswers
        vm.selectedAnswers.observe(viewLifecycleOwner) { sel ->
            submitBtn.isEnabled = sel.isNotEmpty() && vm.isSubmitted.value == false
        }
        submitBtn.setOnClickListener {
            vm.submit()
            adapter.markSubmitted()
        }


        vm.currentQA.observe(viewLifecycleOwner) { qa ->
            qa ?: return@observe
            questionTv.text  = qa.question.questionText

            val multi    = qa.answers.count { it.isCorrect } > 1
            val selected = vm.selectedAnswers.value ?: emptySet()

            adapter.submitList(qa.answers, multi, selected)

            if (vm.isSubmitted.value == true) {
                adapter.markSubmitted()
            }

            playBtn.setImageResource(android.R.drawable.ic_media_play)
        }

        vm.isPlaying.observe(viewLifecycleOwner) { playing ->
            playBtn.setImageResource(
                if (playing) android.R.drawable.ic_media_pause
                else           android.R.drawable.ic_media_play
            )
        }
        playBtn.setOnClickListener {
            vm.togglePlay()
        }

        skipNextBtn.setOnClickListener {
            vm.resetPlayer()
            if (!vm.skip()) {
                findNavController().navigate(
                    QuizGameFragmentDirections
                        .actionQuizPlayFragmentToQuizResultFragment(
                            correctCount = vm.correctCount,
                            totalCount   = vm.totalCount,
                            quizId       = args.quizId
                        )
                )
            }
        }
    }
}
