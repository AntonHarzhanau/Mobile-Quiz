package com.example.musicalquizz.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.musicalquizz.R
import com.example.musicalquizz.databinding.FragmentTrackDetailsBinding
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.data.network.DeezerApi
import com.example.musicalquizz.viewmodel.PlaylistViewModel

import kotlinx.coroutines.launch

class TrackDetailsFragment : Fragment() {
    private val vm: PlaylistViewModel by activityViewModels()
    private var _binding: FragmentTrackDetailsBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get ID of track from Safe Args
        val trackId = TrackDetailsFragmentArgs
            .fromBundle(requireArguments())
            .trackId

        // Load track from Deezer API
        viewLifecycleOwner.lifecycleScope.launch {
            track = DeezerApi.retrofitService.getTrackById(trackId)

            // Show data
            binding.trackTitle.text  = track.title
            binding.trackArtist.text = track.artist.name
            Glide.with(this@TrackDetailsFragment)
                .load(track.album.cover)
                .placeholder(R.drawable.ic_playlist_placeholder)
                .into(binding.trackCover)

            // Play button
            binding.playButton.setOnClickListener {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(track.preview)
                    prepare()
                    start()
                }
            }

            // Add to playlist button
            binding.btnAddToPlaylist.setOnClickListener {
                showPlaylistDialog()
            }
        }
    }

    private fun showPlaylistDialog() {

        vm.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Create playlist first",
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
                        vm.addTrack(selectedPl.id, track)
                        Toast.makeText(
                            requireContext(),
                            "Added to «${selectedPl.title}»",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}

