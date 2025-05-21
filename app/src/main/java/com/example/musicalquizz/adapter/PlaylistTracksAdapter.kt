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
import com.example.musicalquizz.data.db.entities.PlaylistTrackEntity

class PlaylistTracksAdapter(
    private val onClick: (PlaylistTrackEntity) -> Unit
) : ListAdapter<PlaylistTrackEntity, PlaylistTracksAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover: ImageView = view.findViewById(R.id.album_cover)
        private val title: TextView  = view.findViewById(R.id.track_title)
        private val artist: TextView = view.findViewById(R.id.track_artist)

        fun bind(item: PlaylistTrackEntity) {
            title.text = item.title
            artist.text = item.artistName
            Glide.with(itemView)
                .load(item.albumCoverUrl)
                .placeholder(R.drawable.ic_playlist_placeholder)
                .into(cover)
            itemView.setOnClickListener { onClick(item) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PlaylistTrackEntity>() {
        override fun areItemsTheSame(old: PlaylistTrackEntity, new: PlaylistTrackEntity) =
            old.uid == new.uid

        override fun areContentsTheSame(old: PlaylistTrackEntity, new: PlaylistTrackEntity) =
            old == new
    }
}
