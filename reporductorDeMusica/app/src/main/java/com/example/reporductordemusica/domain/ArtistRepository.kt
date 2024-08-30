package com.example.reporductordemusica.domain

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ArtistRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val artistsCollection = firestore.collection("artists")

    suspend fun getArtistById(id: String): Artist? {
        return try {
            val document = artistsCollection.document(id).get().await()
            document.toObject(Artist::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }
}
