package com.example.musicalquizz.ui.fragments

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.SearchAdapter
import com.example.musicalquizz.databinding.FragmentSearchBinding
import com.example.musicalquizz.viewmodel.SearchViewModel
import com.google.android.material.tabs.TabLayout


class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchAdapter
    private var isAlbumMode = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchAdapter(
            onTrackClick = { track ->
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchToTrackDetailsFragment(track.id)
                )
            },
            onAlbumClick = { album ->
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchToAlbumDetailsFragment(album.id)
                )
            }
        )

        binding.searchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResults.adapter = adapter

        // Таб-menu
        binding.searchTabLayout.apply {
            addTab(newTab().setText("Tracks"), true)
            addTab(newTab().setText("Albums"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    isAlbumMode = tab.position == 1
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            setTabTextColors(
                ContextCompat.getColor(context, R.color.gray),
                ContextCompat.getColor(context, R.color.white)
            )
            setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.white))
        }


        binding.searchButton.setOnClickListener { doSearch() }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { doSearch(); true } else false
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun doSearch() {
        val q = binding.searchInput.text.toString().trim()
        if (q.isNotEmpty()) viewModel.search(q, isAlbumMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
