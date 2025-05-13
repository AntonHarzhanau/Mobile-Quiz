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
import com.example.musicalquizz.network.DeezerApi
import kotlinx.coroutines.launch
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.example.musicalquizz.adapter.AlbumTracksAdapter
import com.example.musicalquizz.databinding.FragmentAlbumDetailsBinding
import com.example.musicalquizz.model.Track
import com.example.musicalquizz.network.AlbumWithTracks
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
                    .actionAlbumDetailToTrackDetail(track.id)
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
       vm.playlists.observe(viewLifecycleOwner) { playlists ->
           if (playlists.isNullOrEmpty()) {
               Toast.makeText(
                   requireContext(),
                   "Please create a playlist first",
                   Toast.LENGTH_SHORT
               ).show()
               return@observe
           }

           val titles = playlists.map { it.title }.toTypedArray()

           AlertDialog.Builder(requireContext())
               .setTitle("Choose a playlist")
               .setItems(titles) { _, which ->
                   val selectedPl = playlists[which]

                   // Subscribe to the tracks chosen playlist
                   vm.tracks(selectedPl.id).observe(viewLifecycleOwner) { existingTracks ->
                       val existingIds = existingTracks.map { it.trackId }
                       val newTracks = tracks.filter {it.id !in existingIds}

                       vm.addTracks(selectedPl.id, newTracks)
                       Toast.makeText(
                           requireContext(),
                           "Added ${newTracks.size} track(s) в «${selectedPl.title}»",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }.show()
           }
       }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

