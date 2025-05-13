package com.example.musicalquizz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.db.entities.QuizEntity
import com.example.musicalquizz.model.Quiz

class QuizAdapter(
    private val items: List<QuizEntity>,
    private val onClick: (QuizEntity) -> Unit,
    private val onLongClick: (View, QuizEntity) -> Unit
) : RecyclerView.Adapter<QuizAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView    = itemView.findViewById(R.id.ivCover)
        private val tvTitle: TextView     = itemView.findViewById(R.id.tvQuizTitle)
        private val tvAlbum: TextView     = itemView.findViewById(R.id.tvAlbumName)
        private val tvCount: TextView     = itemView.findViewById(R.id.tvQuestionCount)

        fun bind(entity: QuizEntity) {
            Glide.with(itemView)
                .load(entity.coverUri)
                .into(ivCover)

            tvTitle.text  = entity.title
            tvAlbum.text  = entity.albumName
            tvCount.text  = "Вопросов: ${entity.questionCount}"

            itemView.setOnClickListener   { onClick(entity) }
            itemView.setOnLongClickListener {
                onLongClick(it, entity)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }
}
