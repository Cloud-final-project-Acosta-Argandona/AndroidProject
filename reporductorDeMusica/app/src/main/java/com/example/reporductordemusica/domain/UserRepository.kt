package com.example.reporductordemusica.domain

import android.util.Log
import com.example.reporductordemusica.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun registerUser(email: String, password: String, username: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = UserModel(email, username, emptyList())
                    saveUserToFirestore(user, { onSuccess(email) }, onFailure)
                } else {
                    val exception = task.exception ?: Exception("Authentication error")
                    Log.w("UserRepository", "Authentication failed: ${exception.message}")
                    crashlytics.setCustomKey("email", email)
                    crashlytics.log("Authentication failed")
                    crashlytics.recordException(exception)
                    onFailure(exception)
                }
            }
    }

    fun saveUserToFirestore(user: UserModel, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("users").document(user.email)
            .set(user)
            .addOnSuccessListener {
                onSuccess(user.email)
            }
            .addOnFailureListener { exception ->
                Log.w("UserRepository", "Error saving user: ${exception.message}")
                crashlytics.setCustomKey("user_email", user.email)
                crashlytics.log("Error saving user to Firestore")
                crashlytics.recordException(exception)
                onFailure(exception)
            }
    }

    fun toggleFavoriteSong(userEmail: String, songId: String, onComplete: (Boolean) -> Unit) {
        val userDocRef = firestore.collection("users").document(userEmail)

        firestore.runTransaction { transaction ->
            val userSnapshot = transaction.get(userDocRef)
            val user = userSnapshot.toObject(UserModel::class.java) ?: return@runTransaction false

            val updatedSongs = if (user.idSongs.contains(songId)) {
                user.idSongs - songId
            } else {
                user.idSongs + songId
            }

            Log.d("UserRepository", "Updating user: $userEmail, songId: $songId, newSongsList: $updatedSongs")

            transaction.update(userDocRef, "idSongs", updatedSongs)
            updatedSongs.contains(songId)
        }.addOnSuccessListener { isAdded ->
            Log.d("UserRepository", "Transaction success, isAdded: $isAdded")
            onComplete(isAdded)
        }.addOnFailureListener { exception ->
            Log.w("UserRepository", "Error updating favorites: ${exception.message}")
            crashlytics.setCustomKey("user_email", userEmail)
            crashlytics.setCustomKey("song_id", songId)
            crashlytics.log("Error updating favorite song")
            crashlytics.recordException(exception)
            onComplete(false)
        }
    }

    fun addUserSnapshotListener(userEmail: String, onChanged: (UserModel) -> Unit,
                                onError: (Exception) -> Unit): ListenerRegistration {
        return firestore.collection("users").document(userEmail)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("UserRepository", "Snapshot listener error: ${e.message}")
                    crashlytics.setCustomKey("user_email", userEmail)
                    crashlytics.log("Error in snapshot listener")
                    crashlytics.recordException(e)
                    onError(e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        onChanged(user)
                    } else {
                        val exception = Exception("User data conversion failed")
                        Log.w("UserRepository", "User data conversion failed")
                        crashlytics.setCustomKey("user_email", userEmail)
                        crashlytics.log("Failed to convert user data")
                        crashlytics.recordException(exception)
                        onError(exception)
                    }
                } else {
                    val exception = Exception("No such document")
                    Log.w("UserRepository", "No such document: $userEmail")
                    crashlytics.setCustomKey("user_email", userEmail)
                    crashlytics.log("Document not found")
                    crashlytics.recordException(exception)
                    onError(exception)
                }
            }
    }
}
