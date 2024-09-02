package com.example.reporductordemusica.domain

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ArtistRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val artistsCollection = firestore.collection("artists")
    private val crashlytics = FirebaseCrashlytics.getInstance()

    suspend fun getArtistById(id: String): Artist? {
        return try {
            val document = artistsCollection.document(id).get().await()
            document.toObject(Artist::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            crashlytics.setCustomKey("ArtistID", id)
            crashlytics.setCustomKey("ErrorLocation", "getArtistById")
            crashlytics.recordException(e)
            crashlytics.log("Error fetching artist with ID: $id")
            null
        }
    }
}
