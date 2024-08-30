package com.example.reporductordemusica.domain

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SongRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val songsCollection = firestore.collection("songs")

    suspend fun getFavoriteSongsByIds(ids: List<String>): List<Song> {
        return try {
            val documents = songsCollection
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()

            documents.documents.mapNotNull { document ->
                val song = document.toObject(Song::class.java)
                song?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}
