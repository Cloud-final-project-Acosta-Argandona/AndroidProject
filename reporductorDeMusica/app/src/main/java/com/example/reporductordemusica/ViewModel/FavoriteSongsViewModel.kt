package com.example.reporductordemusica.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reporductordemusica.domain.Song
import com.example.reporductordemusica.domain.SongRepository
import kotlinx.coroutines.launch

class FavoriteSongsViewModel(private val songRepository: SongRepository) : ViewModel() {

    private val _favoriteSongs = MutableLiveData<List<Song>>()
    val favoriteSongs: LiveData<List<Song>> get() = _favoriteSongs

    fun fetchFavoriteSongs(ids: List<String>) {
        viewModelScope.launch {
            val songs = songRepository.getFavoriteSongsByIds(ids)
            _favoriteSongs.postValue(songs)
        }
    }
}
