package com.example.musicalquizz.adapter

import android.media.MediaPlayer
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
import com.example.musicalquizz.model.Track

class SearchAdapter : ListAdapter<Track, SearchAdapter.TrackViewHolder>(DiffCallback()) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackId: Long? = null

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.track_title)
        val artist: TextView = view.findViewById(R.id.track_artist)
        val duration: TextView = view.findViewById(R.id.track_duration)
        val cover: ImageView = view.findViewById(R.id.album_cover)
        val card: View = view.findViewById(R.id.track_card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)

        holder.title.text = track.title
        holder.artist.text = track.artist.name
        holder.duration.text = formatDuration(track.duration.toInt())

        Glide.with(holder.itemView)
            .load(track.album.cover)
            .into(holder.cover)

        holder.card.setOnClickListener {
            if (currentTrackId == track.id && mediaPlayer?.isPlaying == true) {
                // Press again: stop
                mediaPlayer?.pause()
                currentTrackId = null
            } else {
                // New track: stop old and play new
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(track.preview)
                    prepare()
                    start()
                }
                currentTrackId = track.id
            }
        }
    }

    private fun formatDuration(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%d:%02d", min, sec)
    }

    fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    class DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
    }
}
