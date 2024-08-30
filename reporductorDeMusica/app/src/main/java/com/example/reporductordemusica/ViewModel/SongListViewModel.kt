package com.example.reporductordemusica.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reporductordemusica.domain.Artist
import com.example.reporductordemusica.domain.Song
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage

class SongListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _artist = MutableLiveData<Artist?>()
    val artist: MutableLiveData<Artist?> = _artist

    fun fetchSongs(artistId: String) {
        firestore.collection("artists").document(artistId).get()
            .addOnSuccessListener { document ->
                val artist = document.toObject(Artist::class.java)
                _artist.value = artist
            }

        firestore.collection("songs")
            .whereEqualTo("artist", artistId)
            .get()
            .addOnSuccessListener { snapshot ->
                val songs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Song::class.java)
                }
                _songs.value = songs
            }
    }
}