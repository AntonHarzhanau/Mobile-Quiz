package com.example.musicalquizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.PlaylistTracksAdapter
import com.example.musicalquizz.databinding.FragmentPlaylistDetailBinding
import com.example.musicalquizz.db.entities.PlaylistTrackEntity
import com.example.musicalquizz.viewmodel.PlaylistViewModel

class PlaylistDetailFragment : Fragment() {
    private val vm: PlaylistViewModel by activityViewModels()
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: PlaylistTracksAdapter
    private var playlistId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get ID of playlist from SafeArgs
        playlistId = PlaylistDetailFragmentArgs.fromBundle(requireArguments()).playlistId

        // Initialisation adapter and RecyclerView
        trackAdapter = PlaylistTracksAdapter { trackEntity ->
            findNavController().navigate(
                PlaylistDetailFragmentDirections
                    .actionPlaylistDetailToTrackDetailsFragment(trackEntity.trackId)
            )
        }
        binding.rvTracksInPlaylist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        // Observe tracks in playlist
        vm.tracks(playlistId).observe(viewLifecycleOwner, Observer { list: List<PlaylistTrackEntity> ->
            // Refresh cover if there is at least one track
            list.firstOrNull()?.let { item ->
                Glide.with(this)
                    .load(item.albumCoverUrl)
                    .placeholder(R.drawable.ic_playlist_placeholder)
                    .into(binding.ivCover)
            }
            // Pass data to the adapter
            trackAdapter.submitList(list)
        })

        // Add track button -> pass to search fragment
        binding.btnAddTrack.setOnClickListener {
            val action = PlaylistDetailFragmentDirections
                .actionPlaylistDetailToSearch(playlistId)
            findNavController().navigate(action)
        }
        // 5) Play all button
        binding.btnPlayAll.setOnClickListener {
            // TODO: play all tracks from trackAdapter.currentList
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
