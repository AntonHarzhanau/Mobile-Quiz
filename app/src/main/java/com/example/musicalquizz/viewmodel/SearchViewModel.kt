package com.example.musicalquizz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalquizz.model.Track
import com.example.musicalquizz.network.DeezerApi
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel : ViewModel() {
    private val _results = MutableLiveData<List<Track>>()
    val results: LiveData<List<Track>> = _results

    private var currentQuery: String = ""
    private var currentIndex: Int = 0
    private var isLoading = false
    private var allLoaded = false

    fun search(query: String) {
        currentQuery = query
        currentIndex = 0
        allLoaded = false
        _results.value = emptyList()
        loadMore()
    }

    fun loadMore() {
        if (isLoading || allLoaded) return
        isLoading = true

        viewModelScope.launch {
            try {
                val response = DeezerApi.retrofitService.searchTracks(currentQuery, currentIndex)
                val newTracks = response.data
                val currentList = _results.value.orEmpty()
                _results.value = currentList + newTracks
                currentIndex += newTracks.size
                if (newTracks.size < 25) allLoaded = true
            } catch (e: Exception) {
                Timber.Forest.tag("SearchViewModel").e("Search failed: ${e.message}")
                _results.value = emptyList()
            }
            finally {
                isLoading = false
            }
        }
    }
}