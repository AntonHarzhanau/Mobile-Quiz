// QuizDetailsFragment.kt
package com.example.musicalquizz.ui.fragments.quiz

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.databinding.FragmentQuizDetailsBinding
import com.example.musicalquizz.viewmodel.QuizDetailsViewModel

class QuizDetailsFragment : Fragment(R.layout.fragment_quiz_details) {

    private var _binding: FragmentQuizDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: QuizDetailsFragmentArgs by navArgs()
    private val vm: QuizDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentQuizDetailsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.loadQuiz(args.quizId)

        vm.quiz.observe(viewLifecycleOwner) { qwq ->
            binding.tvDetailsTitle.text = qwq.quiz.title
            binding.tvDetailsDescription.text = qwq.quiz.description
            binding.tvDetailsQuestionCount.text = getString(
                R.string.question_count_format,
                qwq.questions.size
            )
            Glide.with(this)
                .load(qwq.quiz.coverUri)
                .into(binding.imgDetailsCover)

            binding.btnStartQuiz.setOnClickListener {
                // TODO: navigation to the game screen (if there is one)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
