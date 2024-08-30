package com.example.reporductordemusica.domain

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SongRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val songsCollection = firestore.collection("songs")

    suspend fun getFavoriteSongsByIds(ids: List<String>): List<Song> {
        val songs = mutableListOf<Song>()
        ids.forEach { id ->
            val documentSnapshot = songsCollection.document(id).get().await()
            documentSnapshot?.let {
                val song = it.toObject(Song::class.java)
                if (song != null) {
                    songs.add(song)
                }
            }
        }
        return songs
    }
}