package com.example.musicalquizz.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquizz.R
import com.example.musicalquizz.adapter.AlbumAdapter
import com.example.musicalquizz.adapter.AlbumTracksAdapter
import com.example.musicalquizz.data.model.Album
import com.example.musicalquizz.data.model.Track
import com.example.musicalquizz.data.network.DeezerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackRecyclerView: RecyclerView

    private lateinit var adapter: AlbumAdapter
    private lateinit var trackAdapter: AlbumTracksAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // albums

        recyclerView = view.findViewById(R.id.horizontal_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AlbumAdapter(emptyList())
        recyclerView.adapter = adapter

        // Tracks
        trackRecyclerView = view.findViewById(R.id.track_recycler_view)
        trackRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackAdapter = AlbumTracksAdapter { track ->
            Toast.makeText(requireContext(), "Track: ${track.title}", Toast.LENGTH_SHORT).show()
        }
        trackRecyclerView.adapter = trackAdapter

        // Fetch data
        fetchAlbums()
        fetchTracks()

        return view
    }

    private fun fetchAlbums() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    DeezerApi.retrofitService.getTopCharts()
                }

                val albums: List<Album> = response.albums.data.take(30)
                adapter.submitList(albums)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Erreur lors du chargement des albums",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun fetchTracks() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    DeezerApi.retrofitService.getTopCharts()
                }

                val tracks: List<Track> = response.tracks.data.take(30)
                trackAdapter.submitList(tracks)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Erreur lors du chargement des morceaux",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
