package com.example.musicalquizz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.data.model.Album
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.data.network.SearchItem


class SearchAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onAlbumClick: (Album) -> Unit
) : ListAdapter<SearchItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_TRACK = 0
        private const val TYPE_ALBUM = 1
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is SearchItem.TrackItem -> TYPE_TRACK
        is SearchItem.AlbumItem -> TYPE_ALBUM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_TRACK) {
            val v = inflater.inflate(R.layout.item_track, parent, false)
            TrackViewHolder(v)
        } else {
            val v = inflater.inflate(R.layout.item_album, parent, false)
            AlbumViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SearchItem.TrackItem -> (holder as TrackViewHolder).bind(item.track)
            is SearchItem.AlbumItem -> (holder as AlbumViewHolder).bind(item.album)
        }
    }

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover: ImageView = view.findViewById(R.id.album_cover)
        private val title: TextView  = view.findViewById(R.id.track_title)
        private val artist: TextView = view.findViewById(R.id.track_artist)

        fun bind(track: Track) {
            title.text = track.title
            artist.text = track.artist.name
            Glide.with(itemView).load(track.album.cover).into(cover)
            itemView.setOnClickListener { onTrackClick(track) }
        }
    }

    inner class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover: ImageView = view.findViewById(R.id.album_cover)
        private val title: TextView  = view.findViewById(R.id.album_title)
        private val artist: TextView = view.findViewById(R.id.album_artist)

        fun bind(album: Album) {
            title.text = album.title
            artist.text = album.artist.name
            Glide.with(itemView).load(album.cover).into(cover)
            itemView.setOnClickListener { onAlbumClick(album) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchItem>() {
        override fun areItemsTheSame(old: SearchItem, new: SearchItem) = when {
            old is SearchItem.TrackItem && new is SearchItem.TrackItem ->
                old.track.id == new.track.id
            old is SearchItem.AlbumItem && new is SearchItem.AlbumItem ->
                old.album.id == new.album.id
            else -> false
        }
        override fun areContentsTheSame(old: SearchItem, new: SearchItem) = old == new
    }
}


