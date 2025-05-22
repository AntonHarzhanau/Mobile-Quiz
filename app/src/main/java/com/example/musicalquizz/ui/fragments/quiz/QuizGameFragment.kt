package com.example.musicalquizz.ui.fragments.quiz

import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicalquizz.R
import com.example.musicalquizz.data.db.AppDatabase
import com.example.musicalquizz.viewmodel.QuizGameViewModel
import com.example.musicalquizz.viewmodel.QuizGameViewModelFactory
import com.example.musicalquizz.data.db.entities.AnswerEntity
import com.example.musicalquizz.data.db.repository.QuestionRepository
import com.example.musicalquizz.data.network.DeezerApi
import androidx.core.net.toUri


class QuizGameFragment : Fragment(R.layout.fragment_quiz_game) {

    private val args: QuizGameFragmentArgs by navArgs()
    private val vm: QuizGameViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).questionDao()
        val api = DeezerApi
        QuizGameViewModelFactory(
            QuestionRepository(dao),
            args.quizId
        )
    }

    private var player: MediaPlayer? = null

    private lateinit var exitBtn: ImageButton
    private lateinit var playBtn: ImageButton
    private lateinit var questionTv: TextView
    private lateinit var answersContainer: LinearLayout
    private lateinit var skipNextBtn: Button
    private lateinit var submitBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitBtn = view.findViewById(R.id.btn_exit)
        playBtn = view.findViewById(R.id.btn_play_preview)
        questionTv = view.findViewById(R.id.tv_question)
        answersContainer = view.findViewById(R.id.answers_container)
        skipNextBtn = view.findViewById(R.id.btn_skip_next)
        submitBtn = view.findViewById(R.id.btn_submit)

        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        // Exit
        exitBtn.setOnClickListener { findNavController().popBackStack() }


        vm.currentQA.observe(viewLifecycleOwner) { qa ->
            qa ?: return@observe
            questionTv.text = qa.question.questionText
            renderAnswers(qa.answers)
            submitBtn.isEnabled = false
            skipNextBtn.text = getString(R.string.skip)
        }

        vm.previewUrl.observe(viewLifecycleOwner) { url ->
            url ?: return@observe
            player?.reset()
            player?.setDataSource(requireContext(), url.toUri())
            player?.setOnPreparedListener { it.start() }
            player?.setOnErrorListener { _, what, _ ->
                Toast.makeText(requireContext(),
                    "Play error: $what", Toast.LENGTH_SHORT).show()
                true
            }
            player?.prepareAsync()
        }

        playBtn.setOnClickListener {
            vm.currentQA.value?.question?.trackId?.let { tid ->
                vm.loadPreview(tid)
            }
        }

        // Skip / Next
        skipNextBtn.setOnClickListener {
            if (!vm.skip()) {
                findNavController().navigate(
                    QuizGameFragmentDirections
                        .actionQuizPlayFragmentToQuizResultFragment(
                            correctCount = vm.correctCount,
                            totalCount = vm.totalCount
                        )
                )
            }
        }

        // Submit
        submitBtn.setOnClickListener {
            vm.submit()
            highlightAnswers()
            submitBtn.isEnabled = false
            skipNextBtn.text = getString(R.string.next)
        }
    }

    private fun renderAnswers(answers: List<AnswerEntity>) {
        answersContainer.removeAllViews()
        val multiple = answers.count { it.isCorrect } > 1

        if (multiple) {
            answers.forEach { ans ->
                CheckBox(requireContext()).apply {
                    text = ans.answerText
                    setOnCheckedChangeListener { _, checked ->
                        vm.toggleAnswer(ans.id, checked)
                        submitBtn.isEnabled = vm.selectedAnswers.value!!.isNotEmpty()
                    }
                    answersContainer.addView(this)
                }
            }
        } else {
            val rg = RadioGroup(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
            }
            answers.forEach { ans ->
                RadioButton(requireContext()).apply {
                    text = ans.answerText
                    id = View.generateViewId()
                    rg.addView(this)
                }
            }
            rg.setOnCheckedChangeListener { _, checkedId ->
                val idx = rg.indexOfChild(rg.findViewById(checkedId))
                vm.toggleAnswer(answers[idx].id, true)
                submitBtn.isEnabled = true
            }
            answersContainer.addView(rg)
        }
    }

    private fun highlightAnswers() {
        vm.currentAnswers.value?.let { answers ->
            answersContainer.children.forEachIndexed { idx, view ->
                val ans = answers[idx]
                val color = when {
                    ans.isCorrect -> Color.GREEN
                    vm.selectedAnswers.value!!.contains(ans.id) -> Color.RED
                    else -> Color.TRANSPARENT
                }
                view.setBackgroundColor(color)
            }
        }
    }

    override fun onDestroyView() {
        player?.release()
        player = null
        super.onDestroyView()
    }
}


