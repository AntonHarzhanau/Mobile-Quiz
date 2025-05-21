package com.example.musicalquizz.ui.fragments.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.PlaylistsAdapter
import com.example.musicalquizz.databinding.FragmentPlaylistBinding
import com.example.musicalquizz.data.db.entities.PlaylistEntity
import com.example.musicalquizz.viewmodel.PlaylistViewModel

class PlaylistsFragment : Fragment() {
    private val vm: PlaylistViewModel by activityViewModels()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Set up the adapter by clicking on the element and the “three dots”
        adapter = PlaylistsAdapter(
            onClick = { pl ->
                // Go to the details of the selected playlist
                val action = PlaylistsFragmentDirections
                    .actionPlaylistsFragmentToPlaylistDetailFragment(pl.id)
                findNavController().navigate(action)
            },
            onMenu = { pl, anchor ->
                showPlaylistPopup(pl, anchor)
            }
        )

        // 2) RecyclerView
        binding.rvPlaylists.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PlaylistsFragment.adapter
        }

        // 3) transition to creating a new playlist
        binding.fabAddPlaylist.setOnClickListener {
            findNavController().navigate(
                PlaylistsFragmentDirections.actionPlaylistsFragmentToCreatePlaylistFragment()
            )
        }

        // 4) Subscribe to LiveData from ViewModel
        vm.playlists.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    private fun showPlaylistPopup(pl: PlaylistEntity, anchor: View) {
        PopupMenu(requireContext(), anchor).apply {
            menuInflater.inflate(R.menu.menu_playlist_item, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        // TODO: implement editing
                        true
                    }
                    R.id.action_delete -> {
                        vm.deletePlaylist(pl)
                        true
                    }
                    R.id.action_create_quiz -> {
                        // TODO: implement creation of a quiz
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
