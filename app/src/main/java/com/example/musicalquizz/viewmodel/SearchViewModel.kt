package com.example.musicalquizz.viewmodel

import androidx.lifecycle.*
import com.example.musicalquizz.data.network.DeezerApi
import kotlinx.coroutines.launch
import com.example.musicalquizz.data.network.SearchItem
import timber.log.Timber

class SearchViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<SearchItem>>()
    val searchResults: LiveData<List<SearchItem>> = _searchResults

    fun search(query: String, isAlbum: Boolean) {
        viewModelScope.launch {
            try {
                if (isAlbum) {
                    val response = DeezerApi.retrofitService.searchAlbums(query)
                    _searchResults.value = response.data.map { SearchItem.AlbumItem(it) }
                } else {
                    val response = DeezerApi.retrofitService.searchTracks(query)
                    _searchResults.value = response.data.map { SearchItem.TrackItem(it) }
                }
            } catch (e: Exception) {
                Timber.tag("SearchViewModel").e("Search error: ${e.message}")
                _searchResults.value = emptyList()
            }
        }
    }
}
