package com.example.musicalquizz.ui.fragments.quiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicalquizz.R


class QuizResultFragment : Fragment(R.layout.fragment_quiz_result) {
    private val args: QuizResultFragmentArgs by navArgs()

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val summary: TextView = v.findViewById(R.id.tv_result_summary)
        val back: Button = v.findViewById(R.id.btn_back)

        summary.text = getString(
            R.string.result_text,
            args.correctCount,
            args.totalCount
        )

        back.setOnClickListener {
            findNavController().navigate(
            QuizResultFragmentDirections
                .actionQuizResultFragmentToQuizDetailFragment(
                    args.quizId
                )
            )

        }
    }
}
