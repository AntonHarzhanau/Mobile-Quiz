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
import com.example.musicalquizz.data.model.Track

class AlbumTracksAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, AlbumTracksAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover = view.findViewById<ImageView>(R.id.album_cover)
        private val title = view.findViewById<TextView>(R.id.track_title)
        private val artist = view.findViewById<TextView>(R.id.track_artist)

        fun bind(track: Track) {
            title.text = track.title
            artist.text = track.artist.name
            Glide.with(itemView).load(track.album.cover).into(cover)
            itemView.setOnClickListener { onTrackClick(track) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(a: Track, b: Track) = a.id == b.id
        override fun areContentsTheSame(a: Track, b: Track) = a == b
    }
}
