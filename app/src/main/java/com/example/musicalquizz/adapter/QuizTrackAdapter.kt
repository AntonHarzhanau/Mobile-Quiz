package com.example.musicalquizz.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.databinding.ItemTrackQuizBinding

class QuizTrackAdapter(
    private val onClick: (Track) -> Unit,
    private val onLongClick: (Track, View) -> Unit
) : ListAdapter<Track, QuizTrackAdapter.TrackVH>(DiffCallback()) {

    private var hasQuestionSet: Set<Long> = emptySet()

    fun setHasQuestions(ids: Set<Long>) {
        hasQuestionSet = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVH {
        val b = ItemTrackQuizBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackVH(b)
    }

    override fun onBindViewHolder(holder: TrackVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackVH(private val b: ItemTrackQuizBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: Track) {
            b.tvTrackTitle.text  = item.title
            b.tvTrackArtist.text = item.artist.name
            Glide.with(b.imgTrackCover)
                .load(item.album.cover)
                .placeholder(R.color.gray_light)
                .into(b.imgTrackCover)

            fun Int.dpToPx(): Int =
                (this * b.root.context.resources.displayMetrics.density).toInt()

            if (hasQuestionSet.contains(item.id)) {
                val strokePx = 4.dpToPx()
                b.card.setStrokeWidth(strokePx)
                b.card.strokeColor = ContextCompat.getColor(
                    b.root.context, R.color.green
                )
            } else {
                b.card.setStrokeWidth(0)
            }

            b.card.setOnClickListener   { onClick(item) }
            b.card.setOnLongClickListener {
                onLongClick(item, it)
                true
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(a: Track, b: Track) = a.id == b.id
        override fun areContentsTheSame(a: Track, b: Track) = a == b
    }
}
