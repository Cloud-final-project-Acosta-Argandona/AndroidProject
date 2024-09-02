package com.example.reporductordemusica.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reporductordemusica.domain.Artist
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ArtistListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _artists = MutableLiveData<MutableList<Artist>>()
    val artists: LiveData<MutableList<Artist>> get() = _artists

    private var registration: ListenerRegistration? = null

    fun fetchArtists() {
        registration = db.collection("artists")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val artistList = snapshot.documents.map { doc ->
                        doc.toObject(Artist::class.java)?.copy(id = doc.id) ?: Artist()
                    }.toMutableList()
                    _artists.value = artistList
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        registration?.remove()
    }
}
