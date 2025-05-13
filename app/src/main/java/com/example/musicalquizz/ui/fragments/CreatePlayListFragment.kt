package com.example.musicalquizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicalquizz.databinding.FragmentCreatePlaylistBinding
import com.example.musicalquizz.viewmodel.PlaylistViewModel

class CreatePlaylistFragment : Fragment() {
    private val vm: PlaylistViewModel by activityViewModels()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc  = binding.etDescription.text.toString().trim()
            vm.createPlaylist(title, desc, /* coverUri= */ null)
            // back to playlists
            findNavController().navigate(
                CreatePlaylistFragmentDirections
                    .actionCreatePlaylistToPlaylists()
            )
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
