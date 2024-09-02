package com.example.reporductordemusica.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reporductordemusica.domain.ArtistRepository
import com.example.reporductordemusica.domain.MediaPlayerManager
import com.example.reporductordemusica.domain.Song
import com.example.reporductordemusica.domain.SongRepository
import com.example.reporductordemusica.domain.UserRepository
import kotlinx.coroutines.launch

class FavoriteSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val songRepository = SongRepository()
    private val artistRepository = ArtistRepository()
    private val mediaPlayerManager = MediaPlayerManager(application)

    private val _favoriteSongs = MutableLiveData<Map<Song, String>>()
    val favoriteSongs: LiveData<Map<Song, String>> get() = _favoriteSongs

    private val _currentlyPlayingSong = MutableLiveData<Song?>()
    val currentlyPlayingSong: LiveData<Song?> get() = _currentlyPlayingSong

    private val _errorMessage = MutableLiveData<String>()

    fun loadFavoriteSongs() {
        val currentUserEmail = userRepository.auth.currentUser?.email.orEmpty()
        if (currentUserEmail.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserDetailsSync(currentUserEmail)
                    val songIds = user.idSongs
                    if (songIds.isNotEmpty()) {
                        val favoriteSongs = songRepository.getFavoriteSongsByIds(songIds)
                        val songArtistMap = mutableMapOf<Song, String>()
                        for (song in favoriteSongs) {
                            val artist = artistRepository.getArtistById(song.artist)
                            songArtistMap[song] = artist?.name.orEmpty()
                        }
                        _favoriteSongs.value = songArtistMap
                    } else {
                        _errorMessage.value = "No favorite songs found for user."
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load favorite songs: ${e.localizedMessage}"
                }
            }
        } else {
            _errorMessage.value = "No user email found."
        }
    }

    fun togglePlayPause(song: Song) {
        val currentSong = _currentlyPlayingSong.value
        if (currentSong == song && mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.pause()
            _currentlyPlayingSong.value = null
        } else {
            if (currentSong != song) {
                playSong(song)
            } else {
                mediaPlayerManager.play()
            }
        }
    }

    private fun playSong(song: Song) {
        val position = mediaPlayerManager.getCurrentPosition()
        mediaPlayerManager.playSong(song.storageUrl, position)
        _currentlyPlayingSong.value = song
    }


    override fun onCleared() {
        super.onCleared()
        mediaPlayerManager.release()
    }
}
