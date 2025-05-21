package com.example.musicalquizz.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.data.network.DeezerApi
import kotlinx.coroutines.launch
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.musicalquizz.adapter.AlbumTracksAdapter
import com.example.musicalquizz.data.db.entities.PlaylistTrackEntity
import com.example.musicalquizz.databinding.FragmentAlbumDetailsBinding
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.data.network.AlbumWithTracks
import com.example.musicalquizz.viewmodel.PlaylistViewModel


class AlbumDetailsFragment : Fragment() {

    private val vm: PlaylistViewModel by activityViewModels()
    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var album: AlbumWithTracks
    private lateinit var trackAdapter: AlbumTracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Initialize RecyclerView with network tracks
        trackAdapter = AlbumTracksAdapter { track ->
            // Open the details of the selected track
            findNavController().navigate(
                AlbumDetailsFragmentDirections
                    .actionAlbumDetailsFragmentToTrackDetailsFragment(track.id)
            )
        }
        binding.albumTrackList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        // 2) Get albumId from Safe Args and load from API
        val albumId = AlbumDetailsFragmentArgs.fromBundle(requireArguments()).albumId
        viewLifecycleOwner.lifecycleScope.launch {
            album = DeezerApi.retrofitService.getAlbumById(albumId)

            // Update the album UI
            binding.albumTitle.text = album.title
            binding.albumArtist.text = album.artist.name
            Glide.with(this@AlbumDetailsFragment)
                .load(album.cover)
                .placeholder(R.drawable.ic_playlist_placeholder)
                .into(binding.albumCover)

            // Display the list of tracks
            trackAdapter.submitList(album.tracks.data)

            // 3) "Add to playlist" button for the entire album
            binding.btnAddAlbumToPlaylist.setOnClickListener {
                showPlaylistDialogForAlbum(album.tracks.data)
            }
        }

    }

    /**
     * Shows a dialog with the names of all playlists,
     * and when selected, adds all the passed [tracks] to it.
     */
    private fun showPlaylistDialogForAlbum(tracks: List<Track>) {
        val playlists = vm.playlists.value.orEmpty()
        if (playlists.isEmpty()) {
            Toast.makeText(requireContext(), "Create a playlist first", Toast.LENGTH_SHORT).show()
            return
        }

        val titles = playlists.map { it.title }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select a playlist")
            .setItems(titles) { _, which ->
                val selected = playlists[which]

                // Here: we observe it just once
                val live = vm.tracks(selected.id)
                val obs = object : Observer<List<PlaylistTrackEntity>> {
                    override fun onChanged(existingTracks: List<PlaylistTrackEntity>) {
                        live.removeObserver(this)  // remove the observer immediately

                        val existingIds = existingTracks.map { it.trackId }.toSet()
                        val newOnes = tracks.filter { it.id !in existingIds }
                        vm.addTracks(selected.id, newOnes)

                        Toast.makeText(
                            requireContext(),
                            "Added ${newOnes.size} tracks to «${selected.title}»",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                live.observe(viewLifecycleOwner, obs)
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

