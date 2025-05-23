package com.example.musicalquizz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.data.db.entities.PlaylistEntity


class PlaylistsAdapter(
    private val onClick: (PlaylistEntity) -> Unit,
    private val onMenu: (PlaylistEntity, View) -> Unit
) : ListAdapter<PlaylistEntity, PlaylistsAdapter.VH>(DiffCallback()) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView     = view.findViewById(R.id.ivCover)
        val tvTitle: TextView      = view.findViewById(R.id.tvTitle)
        val tvCount: TextView      = view.findViewById(R.id.tvCount)
        val btnMenu: ImageButton   = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pl = getItem(position)  // <- PlaylistEntity
        holder.tvTitle.text = pl.title
        holder.tvCount.text = ""
        Glide.with(holder.itemView)
            .load(pl.coverUri ?: R.drawable.ic_playlist_placeholder)
            .into(holder.ivCover)

        holder.itemView.setOnClickListener { onClick(pl) }
        holder.btnMenu.setOnClickListener { onMenu(pl, holder.btnMenu) }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PlaylistEntity>() {
        override fun areItemsTheSame(old: PlaylistEntity, new: PlaylistEntity) =
            old.id == new.id
        override fun areContentsTheSame(old: PlaylistEntity, new: PlaylistEntity) =
            old == new
    }
}

