package com.example.musicalquizz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.data.db.entities.QuizEntity

class QuizListAdapter(
    private val onClick: (QuizEntity) -> Unit,
    private val onLongClick: (QuizEntity, View) -> Unit
) : ListAdapter<QuizEntity, QuizListAdapter.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover : ImageView = itemView.findViewById(R.id.ivCover)
        private val tvTitle : TextView  = itemView.findViewById(R.id.tvQuizTitle)
        private val tvCount : TextView  = itemView.findViewById(R.id.tvQuestionCount)

        fun bind(quiz: QuizEntity) {
            // обложка
            Glide.with(itemView)
                .load(quiz.coverUri)
                .placeholder(R.color.gray_light)
                .into(ivCover)
            // заголовок
            tvTitle.text = quiz.title
            // количество вопросов в QuizEntity храните в поле questionCount
            tvCount.text = itemView.context.getString(
                R.string.question_count_format,
                quiz.questionCount
            )

            itemView.setOnClickListener   { onClick(quiz) }
            itemView.setOnLongClickListener {
                onLongClick(quiz, it)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<QuizEntity>() {
        override fun areItemsTheSame(old: QuizEntity, new: QuizEntity) =
            old.id == new.id
        override fun areContentsTheSame(old: QuizEntity, new: QuizEntity) =
            old == new
    }
}

